package it.unibo.citizenDigitalTwin.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Observable<X> {

    private X value;
    private final Map<Object, Consumer<X>> subscribers;

    public Observable(){
        this.value = null;
        this.subscribers = new ConcurrentHashMap<>();
    }

    public void subscribe(final Object subscriber, final Consumer<X> consumer){
        this.subscribers.put(subscriber, consumer);
    }

    public void unsubscribe(final Object subscriber){
        this.subscribers.remove(subscriber);
    }

    public synchronized void set(final X newValue){
        this.value = newValue;
        this.subscribers.values().forEach(consumer -> consumer.accept(newValue));
    }

    public synchronized X get(){
        return value;
    }

}
