package citizenDT.device.type;

public class BluetoothDevice extends AbstractDevice {
	
	public BluetoothDevice(final String name) {
		super(name);
	}

	@Override
	public CommunicationType getCommunicationType() {
		return CommunicationType.BT;
	}

}
