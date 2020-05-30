package it.unibo.citizenDigitalTwin.artifact;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import java.util.Objects;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.data.device.BluetoothHelper;
import it.unibo.citizenDigitalTwin.data.device.BluetoothHelper.BluetoothState;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class BluetoothArtifact extends JaCaArtifact {

    private static final String PROP_NO_BLUETOOTH = "noBluetooth";
    private static final String PROP_BLUETOOTH_STATE = "bluetoothState";

    private static final int ACTION_BT_ON = 1;

    void init(){
        execInternalOp("initBluetooth");
        //this.subscribeForActivityResults("bluetooth", getId(), "onActivityResult");
    }

    @OPERATION
    void askToTurnOnBluetooth(){
        final Activity activity = getActivity(MainUI.MAIN_UI_ACTIVITY_NAME);
        if(Objects.nonNull(activity)){
            BluetoothHelper.askToTurnOnBluetooth(activity, ACTION_BT_ON);
        }
    }

    /*@OPERATION
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

    }*/

    @INTERNAL_OPERATION
    void initBluetooth(){
        final BluetoothAdapter adapter = BluetoothHelper.getBluetoothAdapter();
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
