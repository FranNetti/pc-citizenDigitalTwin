package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
import it.unibo.citizenDigitalTwin.data.device.DeviceKnowledge;
import it.unibo.citizenDigitalTwin.data.device.SensorKnowledge;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannel;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannels;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that handles the initiation and the end of a connection with an external device
 */
@ARTIFACT_INFO(
        outports = {
                @OUTPORT(name = "technologies")
        }
)
public class DeviceCommunication extends JaCaArtifact {

    private static final String DEVICE_COMMUNICATION_TAG = "[DeviceCommunication]";

    private static final String PROP_CONNECTED_DEVICES = "connectedDevices";
    private static final String PROP_PAIRED_DEVICES = "pairedDevices";
    private static final String PROP_DISCOVERED_DEVICES = "discoveredDevices";
    private static final int UPDATE_PAIRED_DEVICES_TIME = 30000;

    private List<ArtifactId> technologies;
    private boolean work;

    void init(final ArtifactId technologies) {
        this.technologies = Arrays.asList(technologies);
        work = true;

        defineObsProperty(PROP_CONNECTED_DEVICES, new ArrayList<Device>());
        defineObsProperty(PROP_PAIRED_DEVICES, new ArrayList<Device>());
        defineObsProperty(PROP_DISCOVERED_DEVICES, new ArrayList<Device>());
        execInternalOp("updatePairedDevices");
    }

    @OPERATION
    public void connectToDevice(final Device device, final String model, final OpFeedbackParam<Boolean> success) {
        final DeviceKnowledge deviceKnowledge = checkDeviceKnowledge();
        if(Objects.nonNull(deviceKnowledge)){
            //final AtomicBoolean successRes = new AtomicBoolean(false);
            final AtomicBoolean successRes = new AtomicBoolean(true);
            /*this.technologies.forEach(x -> {
                final OpFeedbackParam<Boolean> op = new OpFeedbackParam<>();
                try{
                    execLinkedOp(x, "connectDevice", device, op);
                    successRes.set(successRes.get() | op.get());
                } catch (Exception e){
                    Log.e(DEVICE_COMMUNICATION_TAG, "Error in scanDevices: " + e.getLocalizedMessage());
                }
            });*/
            if(successRes.get()){
                success.set(handleNewDevice(device, deviceKnowledge));
            } else {
                success.set(false);
            }
        } else {
            success.set(false);
        }
    }

    @LINK
    public void disconnectFromDevice(final Device device, final OpFeedbackParam<Boolean> disconnected) {
        final ObsProperty propDevices = getObsProperty(PROP_CONNECTED_DEVICES);
        final List<Device> devices = (List<Device>)propDevices.getValue();

        devices.remove(device);
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
                final Observable<Device> observable = op.get();
                if(Objects.nonNull(observable)){
                    observable.subscribe(DeviceCommunication.this, device -> {
                        if(!devices.contains(device)){
                            devices.add(device);
                            beginExternalSession();
                            updateObsProperty(PROP_DISCOVERED_DEVICES, devices);
                            endExternalSession(true);
                        }
                    });
                }
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
                    final List<Device> devices = op.get();
                    if(Objects.nonNull(devices)){
                        result.addAll(devices);
                    }
                } catch (Exception e){
                    Log.e(DEVICE_COMMUNICATION_TAG, "Error in updatePairDevices: " + e.getLocalizedMessage());
                }
            });
            updateObsProperty(PROP_PAIRED_DEVICES, result);
            await_time(UPDATE_PAIRED_DEVICES_TIME);
        }
    }

    private boolean handleNewDevice(final Device device, final DeviceKnowledge knowledge){
        final ObsProperty propDevices = getObsProperty(PROP_CONNECTED_DEVICES);
        device.setCategories(knowledge.getKnowledge().keySet());
        final List<Device> devices = (List<Device>)propDevices.getValue();
        devices.add(device);
        propDevices.updateValue(devices);
        try{
            final DeviceChannel channel = DeviceChannels.createFromDevice(device);
            final AtomicInteger count = new AtomicInteger(0);
            knowledge.getKnowledge().forEach(((leafCategory, sensorKnowledge) -> {
                final String sensorName = "sensor" + count.getAndIncrement();
                signal("newSensor", device.getName(), sensorName, channel, leafCategory, sensorKnowledge);
            }));
            return true;
        } catch (final Exception e) {
            Log.e(DEVICE_COMMUNICATION_TAG, "Error in handleNewDevice: " + e.getLocalizedMessage());
            return false;
        }
    }

    private DeviceKnowledge checkDeviceKnowledge(){
        final Map<LeafCategory, SensorKnowledge> knowledge = new HashMap<>();
        final SensorKnowledge tempKnowledge = new SensorKnowledge.SensorKnowledgeBuilder()
                .sensorDataIdentifier("body/temperature")
                .reqDataMessage("get/body/temperature")
                .subForDataMessage("subscribe/body/temperature")
                .unitOfMeasure("°C")
                .build();
        final SensorKnowledge spo2Knowledge = new SensorKnowledge.SensorKnowledgeBuilder()
                .sensorDataIdentifier("body/oxygen")
                .reqDataMessage("get/body/oxygen")
                .subForDataMessage("subscribe/body/oxygen")
                .unitOfMeasure("???")
                .build();
        final SensorKnowledge heartRateKnowledge = new SensorKnowledge.SensorKnowledgeBuilder()
                .sensorDataIdentifier("body/heartrate")
                .reqDataMessage("get/body/heartrate")
                .subForDataMessage("subscribe/body/heartrate")
                .unitOfMeasure("???")
                .build();

        knowledge.put(LeafCategory.TEMPERATURE, tempKnowledge);
        knowledge.put(LeafCategory.BLOOD_OXIGEN, spo2Knowledge);
        knowledge.put(LeafCategory.HEART_RATE, heartRateKnowledge);

        return new DeviceKnowledge(knowledge);
    }

}
