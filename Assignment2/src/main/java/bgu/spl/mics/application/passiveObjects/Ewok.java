package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
    int serialNumber;
    boolean available;
    private AtomicBoolean avail;


    public Ewok(int serialNumber){
        this.serialNumber=serialNumber;
        avail = new AtomicBoolean(true);
    }

    public boolean getAvailable(){
        return avail.get();
    }

    /*public boolean tryAcquire(){
        return avail.compareAndSet(true,false);
    }*/

    /**
     * Acquires an Ewok
     */


    public synchronized void acquire() {
        while(!avail.compareAndSet(true,false)){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
        avail.compareAndSet(false,true);
        notifyAll();
    }
}
