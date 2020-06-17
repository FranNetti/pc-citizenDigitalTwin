package it.unibo.citizenDigitalTwin.data.device.communication;

import org.json.JSONException;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.device.type.MockDevice;

/**
 * Mock device channel used for testing porpoises
 */
public class MockDeviceChannel implements DeviceChannel {

    private static final double MIN_TEMP_VALUE = 36.2;
    private static final double MAX_TEMP_VALUE = 39.5;
    private static final double MAX_DIFF = 1;
    private static final double MIN_DIFF = -1;

    private static final int WAIT_TIME = 2 * 60000;

    private final Thread t;
    private final Observable<MsgReceived> msgReceivedObservable;

    public MockDeviceChannel(){
        msgReceivedObservable = new Observable<>();
        t = new Thread(() -> {
            double v = new Random().nextDouble() * (MAX_TEMP_VALUE - MIN_TEMP_VALUE) + MIN_TEMP_VALUE;
            final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
            df.setMaximumFractionDigits(2);
            while(true) {
                try {
                    Thread.sleep(WAIT_TIME);
                    v += new Random().nextDouble() * (MAX_DIFF - MIN_DIFF) + MIN_DIFF;
                    if(v > MAX_TEMP_VALUE) v = MAX_TEMP_VALUE;
                    else if(v < MIN_TEMP_VALUE) v = MIN_TEMP_VALUE;
                    msgReceivedObservable.set(new MsgReceived(
                            "{'type': '" +
                                    MockDevice.MOCK_DEVICE_SENSOR_DATA_IDENTIFIER +
                                    "', 'value': '" + df.format(v) + "', 'isPresent': true}"
                    ));
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void askForData(String reqMessage) {}

    @Override
    public void subscribeToIncomingMessages(Object subscriber, Consumer<MsgReceived> consumer) {
        msgReceivedObservable.subscribe(subscriber, consumer);
    }

    @Override
    public void unsubscribeFromIncomingMessages(Object subscriber) {
        msgReceivedObservable.unsubscribe(subscriber);
    }

    @Override
    public void start() {
        t.start();
    }
}
