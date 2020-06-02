package it.unibo.citizenDigitalTwin.data.device.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.Observable;

/**
 * Class that represent a channel for the Bluetooth technology
 */
public class BluetoothChannel extends Thread implements DeviceChannel {

    private static final String TAG = "[BluetoothCommunicator]";

    private final BluetoothSocket socket;
    private final InputStream inCommunication;
    private final OutputStream outCommunication;
    private boolean work;

    private final Observable<MsgReceived> observable;

    BluetoothChannel(final BluetoothSocket socket) throws IOException {
        Objects.requireNonNull(socket);
        this.socket = socket;
        this.inCommunication = socket.getInputStream();
        this.outCommunication = socket.getOutputStream();
        this.work = true;
        this.observable = new Observable<>();
    }

    public void run() {
        StringBuilder messageReceived = new StringBuilder();
        while (work){
            try{
                if(inCommunication.available() > 0){
                    char c = (char)inCommunication.read();
                    if(c == '\n'){
                        final String stringContent = messageReceived.toString();
                        messageReceived.setLength(0);
                        Log.i(TAG, "Message received: " + stringContent);
                        observable.set(new MsgReceived(stringContent));
                    } else if (c != '\r'){
                        messageReceived = messageReceived.append(c);
                    }
                }
            } catch (final IOException e) {
                work = false;
            } catch (final JSONException e){
                Log.e(TAG, "Error when parsing message: " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void askForData(final String reqMessage) {
        try{
            final String message = reqMessage + '\n';
            outCommunication.write(message.getBytes());
            outCommunication.flush();
        } catch (final IOException e){
            Log.e(TAG, "Error in askForData: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void subscribeToIncomingMessages(final Object subscriber, final Consumer<MsgReceived> consumer){
        this.observable.subscribe(subscriber, consumer);
    }

    @Override
    public void unsubscribeFromIncomingMessages(final Object subscriber){
        this.observable.unsubscribe(subscriber);
    }
}
