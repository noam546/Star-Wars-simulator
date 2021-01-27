package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Comparator;
import java.util.List;

/**
 * class that been sent as marker to the MB in order to notify C3PO and HanSolo that they should attack
 */
public class AttackEvent implements Event<Boolean> {
    private Attack attack ;

    public AttackEvent(List<Integer> serialNumbers, int duration){
        attack = new Attack(serialNumbers,duration);
    }

    public AttackEvent(Attack attack){
        this.attack=attack;
    }

    public Attack getAttack(){
        return attack;
    }

    public void addTotalAttacks(){
        Diary.getInstance().getTotalAttacks().addAndGet(1);//add 1 to the counter of totalattacks
    }
}