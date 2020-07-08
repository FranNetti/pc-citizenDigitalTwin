package it.unibo.citizenDigitalTwin.data;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ObservableTest {

    private static final Integer VALUE = 5;
    private static final Integer DEFAULT_VALUE = -1;
    private static final int MAX_SECONDS_TO_WAIT = 2;

    @Test
    public void isEmptyOnCreation() {
        final Observable<Integer> observable = new Observable<>();
        assertNull(observable.get());
    }

    @Test
    public void setsValueCorrectly() {
        final Observable<Integer> observable = new Observable<>();
        observable.set(VALUE);
        assertEquals(VALUE, observable.get());
    }

    @Test
    public void subscribesToNewValues() {
        final Semaphore semaphore = new Semaphore(0);
        final Observable<Integer> observable = new Observable<>();
        AtomicInteger newValue = new AtomicInteger(DEFAULT_VALUE);
        observable.subscribe(this, val -> {
            newValue.set(val);
            semaphore.release();
        });
        try {
            observable.set(VALUE);
            final boolean acquired = semaphore.tryAcquire(MAX_SECONDS_TO_WAIT, TimeUnit.SECONDS);
            if(acquired){
                assertEquals(VALUE, new Integer(newValue.get()));
                return;
            }
        } catch (final InterruptedException e) {
            System.err.println(e.getLocalizedMessage());
        }
        fail();
    }

    @Test
    public void unsubscribesFromUpdates() {
        final Semaphore semaphore = new Semaphore(0);
        final Observable<Integer> observable = new Observable<>();
        observable.subscribe(this, newValue -> semaphore.release());
        try {
            observable.unsubscribe(this);
            observable.set(VALUE);
            final boolean acquired = semaphore.tryAcquire(MAX_SECONDS_TO_WAIT, TimeUnit.SECONDS);
            assertFalse(acquired);
        } catch (final InterruptedException e) {
            System.err.println(e.getLocalizedMessage());
            fail();
        }
    }

}
