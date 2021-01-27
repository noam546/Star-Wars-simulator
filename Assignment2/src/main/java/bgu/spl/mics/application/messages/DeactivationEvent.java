package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 *   class that been sent as marker to the MB in order to notify R2D2 that he should deactivate the shield
 */
public class DeactivationEvent implements Event<Boolean> {

    public static void stampFinishDeactivate(){
        Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
    }

}
