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
    private static final double MIN_HEART_RATE_VALUE = 60;
    private static final double MAX_HEART_MAX_VALUE = 110;
    private static final double MAX_DIFF = 1;
    private static final double MIN_DIFF = -1;

    private static final int WAIT_TIME = 2000;//2 * 60000;

    private final Thread t;
    private final Observable<MsgReceived> msgReceivedObservable;

    public MockDeviceChannel(){
        msgReceivedObservable = new Observable<>();
        t = new Thread(() -> {
            double tempVal = doubleInRange(MIN_TEMP_VALUE, MAX_TEMP_VALUE);
            int heartRateVal = (int)doubleInRange(MIN_HEART_RATE_VALUE, MAX_HEART_MAX_VALUE);
            final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
            df.setMaximumFractionDigits(2);
            while(true) {
                try {
                    Thread.sleep(WAIT_TIME);
                    tempVal += doubleInRange(MIN_DIFF, MAX_DIFF);
                    heartRateVal += doubleInRange(MIN_DIFF, MAX_DIFF);
                    tempVal = checkIfInRange(tempVal, MIN_TEMP_VALUE, MAX_TEMP_VALUE);
                    heartRateVal = (int)checkIfInRange(heartRateVal, MIN_HEART_RATE_VALUE, MAX_HEART_MAX_VALUE);
                    msgReceivedObservable.set(new MsgReceived(
                            "{'type': '" +
                                    MockDevice.MOCK_DEVICE_TEMPERATURE_SENSOR_DATA_IDENTIFIER +
                                    "', 'value': '" + df.format(tempVal) + "', 'isPresent': true}"
                    ));
                    msgReceivedObservable.set(new MsgReceived(
                            "{'type': '" +
                                    MockDevice.MOCK_DEVICE_HEART_RATE_SENSOR_DATA_IDENTIFIER +
                                    "', 'value': '" + df.format(heartRateVal) + "', 'isPresent': true}"
                    ));
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void askForData(final String reqMessage) { }

    @Override
    public void subscribeToIncomingMessages(final Object subscriber, final Consumer<MsgReceived> consumer) {
        msgReceivedObservable.subscribe(subscriber, consumer);
    }

    @Override
    public void unsubscribeFromIncomingMessages(final Object subscriber) {
        msgReceivedObservable.unsubscribe(subscriber);
    }

    @Override
    public void start() {
        t.start();
    }

    private double doubleInRange(final double minValue, final double maxValue) {
        return new Random().nextDouble() * (maxValue - minValue) + minValue;
    }

    private double checkIfInRange(final double value, final double minValue, final double maxValue) {
        if(value > maxValue) return maxValue;
        else return Math.max(value, minValue);
    }

}
