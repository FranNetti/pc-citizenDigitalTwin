package citizenDT.device.type;

public class BLEDevice extends AbstractDevice {

	public BLEDevice(final String name) {
		super(name);
	}

	@Override
	public CommunicationType getCommunicationType() {
		return CommunicationType.BLE;
	}

}
