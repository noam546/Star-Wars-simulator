package bgu.spl.mics;

import bgu.spl.mics.application.services.LeiaMicroservice;


import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl instance=null;
	private static ConcurrentHashMap <Integer, Vector<Message>> microServiceQueues;
	private static ConcurrentHashMap <Class<? extends Event>,Vector<Integer>> hashMapEventsSubscriptions;        //added by gal
	private static ConcurrentHashMap <Class<? extends Broadcast>, Vector<Integer>> hashMapBroadcastsSubsriptions;
	private static ConcurrentHashMap <Event,Future> futuresHashMap;


	/*private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}*/

	private static void init(){
		microServiceQueues = new ConcurrentHashMap<>();
		hashMapEventsSubscriptions = new ConcurrentHashMap<>();
		hashMapBroadcastsSubsriptions = new ConcurrentHashMap<>();
		futuresHashMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		if(instance==null){
			synchronized (MessageBusImpl.class) {
				if(instance==null) {
					instance = new MessageBusImpl();
					instance.init();
				}
			}
		}
		return instance;
	}

	/*public static MessageBusImpl getInstance(){//added by gal
		return SingletonHolder.instance;
	}*/

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		Vector<Integer> vector = new Vector<>();
		vector.add(m.hashCode());
		if(hashMapEventsSubscriptions.putIfAbsent(type, vector)!=null) {
			hashMapEventsSubscriptions.get(type).add(m.hashCode());
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		Vector<Integer> vector = new Vector<>();
		vector.add(m.hashCode());
		if(hashMapBroadcastsSubsriptions.putIfAbsent(type, vector)!=null) {
			hashMapBroadcastsSubsriptions.get(type).add(m.hashCode());
		}

	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future myCurrFuture = futuresHashMap.get(e);
		myCurrFuture.resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		for(Integer m : hashMapBroadcastsSubsriptions.get(b.getClass())){//sends broadcast to all the subscribed microservices
			if(microServiceQueues.get(m)!=null) {
				microServiceQueues.get(m).add(b);
			}
		}
		notifyAll();
	}


	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if (hashMapEventsSubscriptions.containsKey(e.getClass())) {
			Vector<Integer> tempVec = hashMapEventsSubscriptions.get(e.getClass());
			if(tempVec.size()==0){
				return null;
			}
			Integer microServiceHashCode = roundRobin(tempVec);
			if (microServiceQueues.get(microServiceHashCode) != null) {
				microServiceQueues.get(microServiceHashCode).add(e);
				notifyAll();
				Future<T> newFuture = new Future<>();
				futuresHashMap.put(e, newFuture);
				return newFuture;
			}
		}
		return null;
	}

	////roundRobin function
	private Integer roundRobin(Vector<Integer> microVector){
		Integer output = microVector.remove(0);
		microVector.add(output);
		return output;
	}

	@Override
	public void register(MicroService m) {
		if(!microServiceQueues.containsKey(m.hashCode())) {
			microServiceQueues.put(m.hashCode(), new Vector<Message>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		microServiceQueues.remove(m.hashCode());
		//remove all the broadcasts that m is subscribed to
		for(Vector<Integer> vec : hashMapBroadcastsSubsriptions.values()){
			if(vec.contains(m.hashCode())) {
				Integer mm = m.hashCode();
				vec.remove(mm);
			}
		}
		//remove all the events that m is subscribed to
		for(Vector<Integer> vec : hashMapEventsSubscriptions.values()){
			if(vec.contains(m.hashCode())) {
				Integer mm = m.hashCode();
				vec.remove(mm);
			}
		}

	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		if(microServiceQueues.get(m.hashCode())!=null) {
			while (microServiceQueues.get(m.hashCode()).isEmpty()) {
				wait();
			}
			return microServiceQueues.get(m.hashCode()).remove(0);
		}
		return null;
	}

}