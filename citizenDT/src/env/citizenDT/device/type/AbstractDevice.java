package citizenDT.device.type;

public abstract class AbstractDevice implements Device {

	private final String name;
	
	AbstractDevice(final String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
