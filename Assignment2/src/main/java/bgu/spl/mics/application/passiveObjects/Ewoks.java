package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private static Ewoks instance;
    private static int ewoksNum;
    private static Ewok[] ewokArr;

    public static Ewoks getInstance(int ewoksNum){
        if(instance==null){
            synchronized (Ewoks.class){
                if (instance==null){
                    return instance=new Ewoks(ewoksNum);
                }
            }
        }
        return instance;
    }

    public static Ewoks getInstance(){
        return instance;
    }

    private Ewoks(int _ewoksNum){//for initiallization
        ewoksNum=_ewoksNum;
        ewokArr=new Ewok[ewoksNum+1];
        for(int i=1;i<ewoksNum+1;i++){
            ewokArr[i]=new Ewok(i);
        }
    }

    public Ewok[] getEwokArr(){return ewokArr;}
}
