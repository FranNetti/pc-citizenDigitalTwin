package it.unibo.citizenDigitalTwin.artifact;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.device.BluetoothHelper;
import it.unibo.citizenDigitalTwin.data.device.BluetoothHelper.BluetoothState;
import it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class BluetoothArtifact extends JaCaArtifact {

    private static final String PROP_NO_BLUETOOTH = "noBluetooth";
    private static final String PROP_BLUETOOTH_STATE = "bluetoothState";

    private static final int ACTION_BT_ON = 1;

    private final BluetoothAdapter adapter = BluetoothHelper.getBluetoothAdapter();

    void init(){
        execInternalOp("initBluetooth");
    }

    @OPERATION
    public void askToTurnOnBluetooth(){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            final Activity activity = getActivity(MainUI.MAIN_UI_ACTIVITY_NAME);
            if (Objects.nonNull(activity)) {
                BluetoothHelper.askToTurnOnBluetooth(activity, ACTION_BT_ON);
            }
        }
    }

    @OPERATION
    public void disconnectBTServices(){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            final Activity activity = getActivity(MainUI.MAIN_UI_ACTIVITY_NAME);
            if(Objects.nonNull(activity)){
                BluetoothHelper.unregisterFromBroadcasts(activity);
            }
        }
    }

    @OPERATION
    public void connectToDevice(final BluetoothDevice device, final OpFeedbackParam<Boolean> connectionEstablished){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            final Optional<BluetoothSocket> result = BluetoothHelper.createBluetoothConnectionTo(adapter, device.getAndroidBluetoothDevice());
            if (result.isPresent()) {
                device.setSocket(result.get());
                connectionEstablished.set(true);
            } else {
                connectionEstablished.set(false);
            }
        }
    }

    @OPERATION
    public void disconnectFromDevice(final BluetoothDevice device, final OpFeedbackParam<Boolean> disconnectionSuccessful){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            final Optional<BluetoothSocket> socket = device.getSocket();
            if (socket.isPresent()) {
                BluetoothHelper.closeBluetoothConnection(socket.get());
                disconnectionSuccessful.set(true);
            } else {
                disconnectionSuccessful.set(false);
            }
        }
    }

    @OPERATION
    public void getAvailableDevices(final OpFeedbackParam<Observable<Device>> result){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            final Observable<Device> observable = new Observable<>();
            final Activity activity = getActivity(MainUI.MAIN_UI_ACTIVITY_NAME);
            if(Objects.nonNull(activity)){
                BluetoothHelper.scanAvailableDevices(adapter, activity, deviceFound -> {
                    observable.set(new BluetoothDevice(deviceFound));
                });
            }
            result.set(observable);
        } else {
            result.set(null);
        }
    }

    @OPERATION
    public void getPairedDevices(final OpFeedbackParam<List<Device>> result){
        if(!hasObsProperty(PROP_NO_BLUETOOTH)) {
            result.set(
                    BluetoothHelper.getBluetoothAdapter()
                            .getBondedDevices()
                            .stream()
                            .map(BluetoothDevice::new)
                            .collect(Collectors.toList())
            );
        } else {
            result.set(null);
        }
    }

    @INTERNAL_OPERATION
    protected void initBluetooth(){
        if(Objects.isNull(adapter)){
            defineObsProperty(PROP_NO_BLUETOOTH);
            return;
        }

        defineObsProperty(PROP_BLUETOOTH_STATE, adapter.isEnabled() ? BluetoothState.ON.name() : BluetoothState.OFF.name());
        final Activity activity = getActivity(MainUI.MAIN_UI_ACTIVITY_NAME);
        if(Objects.nonNull(activity)){
            BluetoothHelper.observeBluetoothStateChanges(activity, bluetoothState -> {
                if(Objects.nonNull(bluetoothState)){
                    beginExternalSession();
                    updateObsProperty(PROP_BLUETOOTH_STATE, bluetoothState.name());
                    endExternalSession(true);
                }
            });
        }
    }

}
