package it.unibo.citizenDigitalTwin.data.device.communication;

import org.json.JSONException;

import java.util.Random;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.Observable;

/**
 * Mock device channel used for testing porpoises
 */
public class MockDeviceChannel implements DeviceChannel {

    final Thread t;
    final Observable<MsgReceived> msgReceivedObservable;

    public MockDeviceChannel(){
        msgReceivedObservable = new Observable<>();
        t = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                    double v = new Random().nextDouble() * 39;
                    msgReceivedObservable.set(new MsgReceived("{'type': 'body/temperature', 'value': '" + v + "', 'isPresent': true}"));
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public void askForData(String reqMessage) {
    }

    @Override
    public void subscribeToIncomingMessages(Object subscriber, Consumer<MsgReceived> consumer) {
        msgReceivedObservable.subscribe(subscriber, consumer);
    }

    @Override
    public void unsubscribeFromIncomingMessages(Object subscriber) {
        msgReceivedObservable.unsubscribe(subscriber);
    }
}
