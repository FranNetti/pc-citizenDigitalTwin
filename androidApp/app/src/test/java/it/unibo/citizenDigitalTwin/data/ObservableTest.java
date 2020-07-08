package it.unibo.citizenDigitalTwin.data;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ObservableTest {

    private static final Integer VALUE = 5;

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
        final Observable<Integer> observable = new Observable<>();
        AtomicInteger newValue = new AtomicInteger(-1);
        observable.subscribe(this, newValue::set);
        try {
            Thread.sleep(1000);
            observable.set(VALUE);
            Thread.sleep(3000);
            assertEquals(VALUE, new Integer(newValue.get()));
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void unsubscribesFromUpdates() {
        final Observable<Integer> observable = new Observable<>();
        AtomicBoolean newValueReceived = new AtomicBoolean(false);
        observable.subscribe(this, newValue -> newValueReceived.set(true));
        try {
            Thread.sleep(1000);
            observable.unsubscribe(this);
            observable.set(VALUE);
            Thread.sleep(3000);
            assertFalse(newValueReceived.get());
        } catch (InterruptedException e) {
            fail();
        }
    }

}
