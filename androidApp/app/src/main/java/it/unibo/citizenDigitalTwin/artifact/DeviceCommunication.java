package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cartago.ARTIFACT_INFO;
import cartago.ArtifactId;
import cartago.INTERNAL_OPERATION;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OUTPORT;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.data.device.sensor.MockTemperatureSensor;
import it.unibo.citizenDigitalTwin.data.device.sensor.Sensor;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

@ARTIFACT_INFO(
        outports = {
                @OUTPORT(name = "technologies")
        }
)
public class DeviceCommunication extends JaCaArtifact {

    private static final String DEVICE_COMMUNICATION_TAG = "[DeviceCommunication]";

    private static final String PROP_DEVICES = "connectedDevices";
    private static final String PROP_PAIRED_DEVICES = "pairedDevices";
    private static final String PROP_DISCOVERED_DEVICES = "discoveredDevices";
    private static final String PROP_SENSORS = "sensors";
    private static final int UPDATE_PAIRED_DEVICES_TIME = 30000;

    private List<ArtifactId> technologies;
    private boolean work;

    void init(final ArtifactId technologies) {
        this.technologies = Arrays.asList(technologies);
        work = true;

        defineObsProperty(PROP_SENSORS, new HashMap<LeafCategory, List<Sensor<?>>>());
        defineObsProperty(PROP_DEVICES, new ArrayList<>());
        defineObsProperty(PROP_PAIRED_DEVICES, new ArrayList<>());
        defineObsProperty(PROP_DISCOVERED_DEVICES, new ArrayList<>());
        execInternalOp("updatePairedDevices");
    }

    @LINK
    public void connectToDevice(final Device device, final String model, final OpFeedbackParam<Boolean> success) {
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
    public void disconnectFromDevice(final Device device, final OpFeedbackParam<Boolean> disconnected) {
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
    public void scanForDevices() {
        final List<Device> devices = new ArrayList<>();
        updateObsProperty(PROP_DISCOVERED_DEVICES, devices);
        this.technologies.forEach(x -> {
            final OpFeedbackParam<Observable<Device>> op = new OpFeedbackParam<>();
            try{
                execLinkedOp(x, "getAvailableDevices", op);
                op.get().subscribe(DeviceCommunication.this, device -> {
                    if(!devices.contains(device)){
                        devices.add(device);
                        beginExternalSession();
                        updateObsProperty(PROP_DISCOVERED_DEVICES, devices);
                        endExternalSession(true);
                    }
                });
            } catch (Exception e){
                Log.e(DEVICE_COMMUNICATION_TAG, "Error in scanDevices: " + e.getLocalizedMessage());
            }
        });
    }

    @INTERNAL_OPERATION
    public void updatePairedDevices(){
        while(work){
            final List<Device> result = new ArrayList<>();
            this.technologies.forEach(x -> {
                final OpFeedbackParam<List<Device>> op = new OpFeedbackParam<>();
                try{
                    execLinkedOp(x, "getPairedDevices", op);
                    result.addAll(op.get());
                } catch (Exception e){
                    Log.e(DEVICE_COMMUNICATION_TAG, "Error in updatePairDevices: " + e.getLocalizedMessage());
                }
            });
            updateObsProperty(PROP_PAIRED_DEVICES, result);
            await_time(UPDATE_PAIRED_DEVICES_TIME);
        }
    }

}
