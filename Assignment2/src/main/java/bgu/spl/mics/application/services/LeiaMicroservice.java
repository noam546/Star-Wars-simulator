package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.terminateEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

import static bgu.spl.mics.application.passiveObjects.Diary.cdl;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Future[] futures;
    private int completedEvents;


    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		futures=new Future[attacks.length+2];
        completedEvents = 0;
    }

    @Override
    protected void initialize() {
        try {


            while(Diary.cdl.getCount()>0) {
                Diary.cdl.await();//wait till c3po and han solo register
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        subscribeBroadcast(terminateEvent.class, callBackFunction -> terminate());
        for(int i=0;i<attacks.length;i++){
            AttackEvent attackEvent=new AttackEvent(attacks[i]);
            futures[i]=sendEvent(attackEvent);
        }
        for(Future current: futures){
            if(current!=null) {
                current.get();
                completedEvents++;
                if (completedEvents == attacks.length) {
                    futures[completedEvents] = sendEvent(new DeactivationEvent());
                }
                if (completedEvents == attacks.length + 1) {
                    futures[completedEvents] = sendEvent(new BombDestroyerEvent());
                }
                if (completedEvents == attacks.length + 2) {
                    sendBroadcast(new terminateEvent());
                }
            }
        }

    }


    @Override
    protected void close() {
        Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
    }
}
