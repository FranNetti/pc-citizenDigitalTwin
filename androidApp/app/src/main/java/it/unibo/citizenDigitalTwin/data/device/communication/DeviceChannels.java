package it.unibo.citizenDigitalTwin.data.device.communication;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice;
import it.unibo.citizenDigitalTwin.data.device.type.Device;

/**
 * Helper class for {@link DeviceChannel}
 */
public class DeviceChannels {

    /**
     * Create a new device channel from a specified device
     * @param device the device that you want to communicate with
     * @return a new device channel
     * @throws IOException if an exception occurs
     */
    public static DeviceChannel createFromDevice(final Device device) throws IOException {
        switch (device.getCommunicationType()){
            case BT:
                final Optional<BluetoothSocket> socket = ((BluetoothDevice)device).getSocket();
                return socket.isPresent() ? new BluetoothChannel(socket.get()) : null;
            case MOCK: return new MockDeviceChannel();
            default: return null;
        }
    }

}
