package it.unibo.citizenDigitalTwin.data.device.type;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class BluetoothDevice extends AbstractDevice {

    public BluetoothDevice(final String name, final List<LeafCategory> categories) {
        super(name, categories);
    }

    @Override
    public CommunicationType getCommunicationType() {
        return CommunicationType.BT;
    }

}
