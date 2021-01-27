package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.terminateEvent;
import bgu.spl.mics.application.passiveObjects.Diary;



/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        subscribeEvent(DeactivationEvent.class, (DeactivationEvent dEvent) -> {
            Thread.sleep(duration);
            DeactivationEvent.stampFinishDeactivate();
            complete(dEvent,true);
        });
        subscribeBroadcast(terminateEvent.class, c->terminate());
        Diary.cdl.countDown();
    }



    @Override
    protected void close() {
        Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
    }

}
