package it.unibo.citizenDigitalTwin.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Class that represent an information, where you want to be notified each time a new value has been published
 * @param <X>
 */
public class Observable<X> {

    private X value;
    private final Map<Object, Consumer<X>> subscribers;

    public Observable(){
        this.value = null;
        this.subscribers = new ConcurrentHashMap<>();
    }

    /**
     * Subscribe for changes of the value
     * @param subscriber the subscriber
     * @param consumer how the value has to be used each time a new change occurs
     */
    public void subscribe(final Object subscriber, final Consumer<X> consumer){
        this.subscribers.put(subscriber, consumer);
    }

    /**
     * Unsubscribe from a previous subscription
     * @param subscriber the subscriber
     */
    public void unsubscribe(final Object subscriber){
        this.subscribers.remove(subscriber);
    }

    /**
     * Setter
     * @param newValue the new value for the information
     */
    public synchronized void set(final X newValue){
        this.value = newValue;
        this.subscribers.values().forEach(consumer -> consumer.accept(newValue));
    }

    /**
     * Getter
     * @return the current value, it may be null
     */
    public synchronized X get(){
        return value;
    }

}
