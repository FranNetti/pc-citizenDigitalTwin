package it.unibo.citizenDigitalTwin.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cartago.LINK;
import cartago.OPERATION;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.device.type.BLEDevice;
import it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.data.device.sensor.MockTemperatureSensor;
import it.unibo.citizenDigitalTwin.data.device.sensor.Sensor;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class DeviceCommunication extends JaCaArtifact {

    private static final String PROP_DEVICES = "connectedDevices";
    private static final String PROP_SENSORS = "sensors";

    void init() {
        final List<Device> devices =  new ArrayList<>(Arrays.asList(
                new BluetoothDevice("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new BluetoothDevice("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new BluetoothDevice("Termometro", Arrays.asList(LeafCategory.TEMPERATURE))
        ));

        defineObsProperty(PROP_SENSORS, new HashMap<LeafCategory, List<Sensor<?>>>());
        defineObsProperty(PROP_DEVICES, devices);
    }

    @LINK
    void addDevice(final Device device, final String model, final OpFeedbackParam<Boolean> success) {
        final ObsProperty propSensors = getObsProperty(PROP_SENSORS);
        final Map<Device,List<Sensor<?>>> sensors = (Map<Device, List<Sensor<?>>>)propSensors.getValue();
        final List<Sensor<?>> deviceSensors = Arrays.asList(new MockTemperatureSensor());
        final ObsProperty propDevices = getObsProperty(PROP_DEVICES);
        final List<Device> devices = (List<Device>)propDevices.getValue();
        if (deviceSensors.isEmpty()) {
            success.set(false);
        } else {
            sensors.put(device, deviceSensors);
            devices.add(device);
            propSensors.updateValue(sensors);
            propDevices.updateValue(devices);
            signal("newDevice",model);
            success.set(true);
        }
    }

    @LINK
    void removeDevice(final Device device, final OpFeedbackParam<Boolean> disconnected) {
        final ObsProperty propSensors = getObsProperty(PROP_SENSORS);
        final Map<Device,List<Sensor<?>>> sensors = (Map<Device, List<Sensor<?>>>)propSensors.getValue();
        final ObsProperty propDevices = getObsProperty(PROP_DEVICES);
        final List<Device> devices = (List<Device>)propDevices.getValue();

        sensors.remove(device);
        devices.remove(device);
        propSensors.updateValue(sensors);
        propDevices.updateValue(devices);
        disconnected.set(true);
    }

    @OPERATION
    void availableDevices(final OpFeedbackParam<Set<Device>> devices) {
        devices.set(new HashSet<>(Arrays.asList(
                new BLEDevice("Disponibile-1", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new BluetoothDevice("Disponibile-2", Arrays.asList(LeafCategory.HEART_RATE)),
                new BLEDevice("Disponibile-3", Arrays.asList(LeafCategory.TEMPERATURE))
        )));
    }

}
