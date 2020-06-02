package it.unibo.citizenDigitalTwin.data.device.communication;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice;
import it.unibo.citizenDigitalTwin.data.device.type.Device;

public class DeviceChannels {

    public static DeviceChannel createFromDevice(final Device device) throws IOException {
        /*switch (device.getCommunicationType()){
            case BT:
                final Optional<BluetoothSocket> socket = ((BluetoothDevice)device).getSocket();
                return socket.isPresent() ? new BluetoothChannel(socket.get()) : null;
            default: return null;
        }*/

        return new DeviceChannelStub();
    }

}
