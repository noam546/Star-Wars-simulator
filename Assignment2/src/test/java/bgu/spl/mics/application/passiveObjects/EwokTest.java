package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    Ewok e;

    @BeforeEach
    void setUp() {
        e=new Ewok(1);

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void acquire() {
        e.acquire();
        assertEquals(false, e.available);

    }

    @Test
    void release() {
        e.available=false;
        e.release();
        assertEquals(true, e.available);
    }
}