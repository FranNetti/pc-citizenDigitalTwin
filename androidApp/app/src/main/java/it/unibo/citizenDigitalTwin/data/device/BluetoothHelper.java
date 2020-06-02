package it.unibo.citizenDigitalTwin.data.device;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import androidx.core.app.ActivityCompat;

/**
 * Helper class for the Bluetooth use
 */
public class BluetoothHelper {

    public enum BluetoothState {
        ON, OFF, TURNING_ON, TURNING_OFF;
    }

    private static final String BLUETOOTH_HELPER_TAG = "[BluetoothHelper]";
    private static final int DISCOVERABILITY_TIME = 300; //5 minutes

    private static final UUID UUID_DEFAULT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BL_USE_REQ_CODE = 22;

    private static final List<BroadcastReceiver> receivers = new ArrayList<>();

    public static BluetoothAdapter getBluetoothAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Register an activity for changes in Bluetooth state
     * @param activity the activity you want to register
     * @param consumer a consumer that handles the new Bluetooth state
     * @return an id that identifies the observe operation
     */
    public static int observeBluetoothStateChanges(final Activity activity, final Consumer<BluetoothState> consumer){
        Objects.requireNonNull(activity);
        Objects.requireNonNull(consumer);
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                final BluetoothHelper.BluetoothState newState;
                switch(state) {
                    case BluetoothAdapter.STATE_OFF: newState = BluetoothState.OFF; break;
                    case BluetoothAdapter.STATE_ON: newState = BluetoothState.ON; break;
                    case BluetoothAdapter.STATE_TURNING_OFF: newState = BluetoothState.TURNING_OFF; break;
                    case BluetoothAdapter.STATE_TURNING_ON: newState = BluetoothState.TURNING_ON; break;
                    default: newState = null; break;
                }
                consumer.accept(newState);
            }
        };
        final IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(receiver, filter);
        receivers.add(receiver);
        return receivers.size() - 1;
    }

    /**
     * Ask the user to turn on the Bluetooth
     * @param activity the running activity
     * @param operationIdentifier the operation identifier to use for the method 'Activity.onActivityResult'
     */
    public static void askToTurnOnBluetooth(final Activity activity, final int operationIdentifier){
        Objects.requireNonNull(activity);
        final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, operationIdentifier);
    }

    /**
     * Start a Bluetooth scan for the available devices
     * @param adapter the Bluetooth adapter
     * @param activity the running activity
     * @param consumer a consumer that handles the device discovered
     * @return an id that identifies the scan operation
     */
    public static int scanAvailableDevices(final BluetoothAdapter adapter, final Activity activity, final Consumer<BluetoothDevice> consumer){
        //needed if the application didn't requested
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BL_USE_REQ_CODE);
        adapter.startDiscovery();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    consumer.accept(device);
                }
            }
        };
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);
        receivers.add(receiver);
        return receivers.size() - 1;
    }

    /**
     * Enable the Bluetooth discoverability for the application device
     * @param activity the running activity
     */
    public static void enableDeviceDiscoverability(final Activity activity){
        final Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_TIME);
        activity.startActivity(discoverableIntent);
    }

    /**
     * Connect to a bluetooth device
     * @param adapter the bluetooth adapter
     * @param device the device to connect to
     * @return if the connection has been established or not
     */
    public static Optional<BluetoothSocket> createBluetoothConnectionTo(final BluetoothAdapter adapter, final BluetoothDevice device) {
        final BluetoothSocket socket;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID_DEFAULT);
            adapter.cancelDiscovery();
            socket.connect();
        } catch (final IOException exception){
            Log.e(BLUETOOTH_HELPER_TAG, "Exception in createBluetoothConnectionTo: " + exception.getLocalizedMessage());
            return Optional.empty();
        }
        return Optional.of(socket);
    }

    /**
     * Close the bluetooth connection to a device
     * @param socket the bluetooth socket for the communication
     */
    public static void closeBluetoothConnection(final BluetoothSocket socket){
        try{
            socket.close();
        } catch (final IOException exception){
            Log.e(BLUETOOTH_HELPER_TAG, "Exception in closeBluetoothConnection: " + exception.getLocalizedMessage());
        }
    }

    /**
     * Unregister from the scan of available devices
     * @param activity the running activity, from whom started the request
     * @param consumerId the id returned by {@link #scanAvailableDevices}
     */
    public static void unregisterFromScan(final Activity activity, final int consumerId){
        activity.unregisterReceiver(receivers.get(consumerId));
        receivers.remove(consumerId);
    }

    /**
     * Unregister from all the services
     * @param activity the running activity, from whom started the request
     */
    public static void unregisterFromBroadcasts(final Activity activity){
        receivers.forEach(activity::unregisterReceiver);
        receivers.clear();
    }

}
