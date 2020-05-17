// CArtAgO artifact code for project citizenDT

package citizenDT.device;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cartago.*;
import citizenDT.device.sensor.MockTemperatureSensor;
import citizenDT.device.sensor.Sensor;
import citizenDT.device.type.BLEDevice;
import citizenDT.device.type.BluetoothDevice;
import citizenDT.device.type.Device;

public class DeviceCommunication extends Artifact {
	
	private static final String PROP_SENSORS = "sensors";
	
	void init() {
		defineObsProperty(PROP_SENSORS, new HashMap<Device,List<Sensor<?>>>());
	}

	@LINK
	void addDevice(final Device device, final String model) {
		final ObsProperty propSensors = getObsProperty(PROP_SENSORS);
		final Map<Device,List<Sensor<?>>> sensors = (Map<Device,List<Sensor<?>>>)propSensors.getValue();
		final List<Sensor<?>> deviceSensors = Arrays.asList(new MockTemperatureSensor());
		if (deviceSensors.isEmpty())
			signal("addDeviceFailed", model);
		else {
			sensors.put(device, deviceSensors);
			propSensors.updateValue(sensors);
			signal("newDevice",model);
		}
	}
	
	@LINK
	void removeDevice(final Device device, final OpFeedbackParam<Boolean> disconnected) {
		disconnected.set(true);
	}
	
	@OPERATION
	void availableDevices(final OpFeedbackParam<Set<Device>> devices) {
		devices.set(Arrays.asList(
						new BLEDevice("braccialetto"),
						new BluetoothDevice("petto"),
						new BLEDevice("orologio")
					).stream()
					.collect(Collectors.toSet())
				);
	}

}

