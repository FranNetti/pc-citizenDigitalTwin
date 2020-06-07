package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import cartago.ARTIFACT_INFO;
import cartago.ArtifactId;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OUTPORT;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.DeviceKnowledgeResponse;
import it.unibo.citizenDigitalTwin.data.device.ConnectionResult;
import it.unibo.citizenDigitalTwin.data.device.DeviceKnowledge;
import it.unibo.citizenDigitalTwin.data.device.SensorKnowledge;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannel;
import it.unibo.citizenDigitalTwin.data.device.communication.DeviceChannels;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.data.device.type.MockDevice;
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
    private static final String SIGNAL_NEW_SENSOR = "newSensor";
    private static final String SIGNAL_DEVICE_DISCONNECTED = "deviceDisconnected";
    private static final int UPDATE_PAIRED_DEVICES_TIME = 30000;

    private List<ArtifactId> technologies;
    private ArtifactId deviceKnowledgeArtifact;
    private boolean work;

    void init(final ArtifactId deviceKnowledgeArtifact, final Object[] technologies) {
        this.deviceKnowledgeArtifact = deviceKnowledgeArtifact;
        this.technologies = new ArrayList<>();
        for(Object tec : technologies){
            if(tec instanceof ArtifactId){
                this.technologies.add((ArtifactId)tec);
            }
        }
        work = true;

        defineObsProperty(PROP_CONNECTED_DEVICES, new ArrayList<Device>());
        defineObsProperty(PROP_PAIRED_DEVICES, new ArrayList<Device>());
        defineObsProperty(PROP_DISCOVERED_DEVICES, new ArrayList<Device>());
        execInternalOp("updatePairedDevices");
    }

    @OPERATION
    public void connectToDevice(final Device device, final String model, final OpFeedbackParam<ConnectionResult> success) {
        final ObsProperty propDevices = getObsProperty(PROP_CONNECTED_DEVICES);
        final List<Device> devices = (List<Device>)propDevices.getValue();
        if(devices.contains(device)){
            success.set(ConnectionResult.FAILURE);
            return;
        }
        final OpFeedbackParam<DeviceKnowledgeResponse> response = new OpFeedbackParam<>();
        try{
            execLinkedOp(deviceKnowledgeArtifact, "findDeviceKnowledge", model, response);
            final DeviceKnowledgeResponse deviceKnowledgeResponse = response.get();
            if(deviceKnowledgeResponse.isSuccessful()){
                handleConnectionToDevice(device, deviceKnowledgeResponse.getKnowledge().get(), success);
            } else {
                success.set(ConnectionResult.getFromDeviceKnowledgeResponse(deviceKnowledgeResponse));
            }
        } catch (final Exception e){
            Log.e(DEVICE_COMMUNICATION_TAG, "Error in scanDevices: " + e.getLocalizedMessage());
        }
    }

    @OPERATION
    public void disconnectFromDevice(final Device device, final OpFeedbackParam<Boolean> disconnected) {
        final AtomicBoolean success = new AtomicBoolean(false);
        if(device instanceof MockDevice){
            success.set(true);
        } else {
            this.technologies.forEach(x -> {
                final OpFeedbackParam<Boolean> op = new OpFeedbackParam<>();
                try{
                    execLinkedOp(x, "disconnectDevice", device, op);
                    success.set(success.get() | op.get());
                } catch (Exception e){
                    Log.e(DEVICE_COMMUNICATION_TAG, "Error in scanDevices: " + e.getLocalizedMessage());
                }
            });
        }
        if(success.get()){
            final ObsProperty propDevices = getObsProperty(PROP_CONNECTED_DEVICES);
            final List<Device> devices = (List<Device>)propDevices.getValue();
            devices.remove(device);
            propDevices.updateValue(devices);
            signal(SIGNAL_DEVICE_DISCONNECTED, device.getName());
        }
        disconnected.set(success.get());
    }

    @OPERATION
    public void scanForDevices() {
        final List<Device> devices = new ArrayList<>();
        updateObsProperty(PROP_DISCOVERED_DEVICES, devices);
        devices.add(new MockDevice());
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

    private void handleConnectionToDevice(final Device device, DeviceKnowledge knowledge, final OpFeedbackParam<ConnectionResult> success){
        final AtomicBoolean successRes = new AtomicBoolean(device instanceof MockDevice);
        this.technologies.forEach(x -> {
            final OpFeedbackParam<Boolean> op = new OpFeedbackParam<>();
            try{
                execLinkedOp(x, "connectDevice", device, op);
                successRes.set(successRes.get() | op.get());
            } catch (final Exception e){
                Log.e(DEVICE_COMMUNICATION_TAG, "Error in private connectToDevice: " + e.getLocalizedMessage());
            }
        });
        if(successRes.get()){
            success.set(handleNewDevice(device, knowledge));
        } else {
            success.set(ConnectionResult.FAILURE);
        }
    }

    private ConnectionResult handleNewDevice(final Device device, final DeviceKnowledge knowledge){
        final ObsProperty propDevices = getObsProperty(PROP_CONNECTED_DEVICES);
        device.setCategories(
                knowledge.getSensorKnowledge()
                        .stream()
                        .map(SensorKnowledge::getLeafCategory)
                        .collect(Collectors.toList())
        );
        final List<Device> devices = (List<Device>)propDevices.getValue();
        devices.add(device);
        propDevices.updateValue(devices);
        try{
            final DeviceChannel channel = DeviceChannels.createFromDevice(device);
            channel.start();
            final AtomicInteger count = new AtomicInteger(0);
            final String baseName = device.getName() + "-sensor";
            knowledge.getSensorKnowledge().forEach(sensorKnowledge -> {
                final String sensorName = baseName + count.getAndIncrement();
                signal(SIGNAL_NEW_SENSOR, device.getName(), sensorName, channel, sensorKnowledge);
            });
            return ConnectionResult.SUCCESS;
        } catch (final Exception e) {
            Log.e(DEVICE_COMMUNICATION_TAG, "Error in handleNewDevice: " + e.getLocalizedMessage());
            return ConnectionResult.FAILURE;
        }
    }

}
