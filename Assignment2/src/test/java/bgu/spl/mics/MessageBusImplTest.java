package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.ExampleBroadcast;
import bgu.spl.mics.application.messages.ExampleEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    MessageBusImpl mbi=MessageBusImpl.getInstance();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {


    }

    @Test
    void subscribeEvent() {
        MicroService microService=new HanSoloMicroservice(){};
        mbi.subscribeEvent(ExampleEvent.class,microService);
        mbi.register(microService);
        ExampleEvent e=new ExampleEvent(1);
        microService.sendEvent(e);
        try {
            assertEquals(e,mbi.awaitMessage(microService));//check if microservice got the message e
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        mbi.unregister(microService);
    }

    @Test
    void subscribeBroadcast() {
        MicroService microService=new HanSoloMicroservice() {};
        mbi.subscribeBroadcast(ExampleBroadcast.class,microService);
        mbi.register(microService);
        ExampleBroadcast b = new ExampleBroadcast(1);
        microService.sendBroadcast(b);
        try {
            assertEquals(b,mbi.awaitMessage(microService));//check if microservice got the message e
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        mbi.unregister(microService);

    }

    @Test
    void complete() {
        //in order to send event and make him complete we need some MicroService to subscribe the event and handle it
        C3POMicroservice m = new C3POMicroservice();
        mbi.subscribeEvent(ExampleEvent.class,m);
        mbi.register(m);
        ExampleEvent exampleEvent = new ExampleEvent(1);
        Future<Integer> future = mbi.sendEvent(exampleEvent);
        Integer result = new Integer(1);
        mbi.complete(exampleEvent,result);
        assertEquals(result,future.get());
        mbi.unregister(m);
    }

    @Test
    void sendBroadcast() {
        //this function is tested in the sendBroadcast test
    }

    @Test
    void sendEvent() {
        //this function is tested in the subscribeEvent test
    }

    @Test
    void register() {
        //this function is tested in all the other tests
    }

    @Test
    void unregister() {
//this function is tested in all the other tests
    }

    @Test
    void awaitMessage() {
        //register and subscribe some MicroService so the event will be sent to it
        C3POMicroservice microService = new C3POMicroservice();
        mbi.subscribeEvent(ExampleEvent.class,microService);
        mbi.register(microService);
        ExampleEvent e=new ExampleEvent(1);
        microService.sendEvent(e);
        try {
            assertEquals(e,mbi.awaitMessage(microService));//check if microservice got the message e
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        mbi.unregister(microService);

    }
}