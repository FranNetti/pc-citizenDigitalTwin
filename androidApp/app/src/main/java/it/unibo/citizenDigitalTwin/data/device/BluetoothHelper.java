package it.unibo.citizenDigitalTwin.data.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.device.type.Device;

/**
 * Helper class for the Bluetooth use
 */
public class BluetoothHelper {

    public enum BluetoothState {
        ON, OFF, TURNING_ON, TURNING_OFF;
    }

    private static final List<BroadcastReceiver> receivers = new ArrayList<>();

    public static BluetoothAdapter getBluetoothAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Register an activity for changes in Bluetooth state
     * @param activity the activity you want to register
     * @param consumer a consumer that handles the new Bluetooth state
     */
    public static void observeBluetoothStateChanges(final Activity activity, final Consumer<BluetoothState> consumer){
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
    }

    /**
     * Ask the user to turn on the Bluetooth
     * @param activity the running activity
     * @param operationIdentifier the operation identifier to use for the method {@link Activity#onActivityResult}
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
    public static int scanAvailableDevices(final BluetoothAdapter adapter, final Activity activity, final Consumer<Device> consumer){
        adapter.startDiscovery();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    consumer.accept(
                            new it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice(
                                    device,
                                    new ArrayList<>()
                            )
                    );
                }
            }
        };
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);
        receivers.add(receiver);
        return receivers.size() - 1;
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

}
