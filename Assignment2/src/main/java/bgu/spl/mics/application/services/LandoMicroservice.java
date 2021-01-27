package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.terminateEvent;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(BombDestroyerEvent.class,(BombDestroyerEvent bDBroadcast) -> {
            Thread.sleep(duration);
            complete(bDBroadcast,true);
        });
        subscribeBroadcast(terminateEvent.class, c -> terminate());
        Diary.cdl.countDown();//let leia know that lando registered

    }

    @Override
    protected void close() {
        Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
    }
}
