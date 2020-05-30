package it.unibo.citizenDigitalTwin.data.device.type;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class BluetoothDevice extends AbstractDevice {

    private android.bluetooth.BluetoothDevice device;

    public BluetoothDevice(final android.bluetooth.BluetoothDevice device, final List<LeafCategory> categories) {
        super(device.getName(), categories);
        this.device = device;
    }

    public BluetoothDevice(final String name, final List<LeafCategory> categories) {
        super(name, categories);
    }

    @Override
    public CommunicationType getCommunicationType() {
        return CommunicationType.BT;
    }

}
