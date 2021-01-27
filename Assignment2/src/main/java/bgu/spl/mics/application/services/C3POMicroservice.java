package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.messages.terminateEvent;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;



/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (AttackEvent aEvent)->callBackAttack(aEvent));// subscribe Han to Attack Events
        subscribeBroadcast(terminateEvent.class, callBackFunction -> terminate());
        Diary.cdl.countDown();//let leia know that c3po or hansolo registered


    }

    private void callBackAttack(AttackEvent aEvent){

        List<Integer> ewokRequires = aEvent.getAttack().getSerials();
        ////sort the list in order to prevent deadlock
        ewokRequires.sort(Integer::compareTo);
        Ewok[] ewoksArray = Ewoks.getInstance().getEwokArr();
        for(Integer currEwok: ewokRequires){
            ewoksArray[currEwok].acquire();
        }
        try {
            Thread.sleep(aEvent.getAttack().getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Diary.getInstance().setC3POFinish(System.currentTimeMillis());
        complete(aEvent,true);
        for(Integer currEwok: ewokRequires){
            ewoksArray[currEwok].release();
        }
        aEvent.addTotalAttacks();//increment 1 attack in totalAttacks
    }

    @Override
    protected void close() {
        Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
    }
}
