package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import org.json.JSONException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cartago.LINK;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import it.unibo.citizenDigitalTwin.data.connection.channel.CommunicationChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.MockDeviceKnowledgeChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.DeviceKnowledgeResponse;
import it.unibo.citizenDigitalTwin.data.device.DeviceKnowledge;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class DeviceKnowledgeArtifact extends JaCaArtifact {

    private static final String TAG = "[DeviceKnowledge]";
    private static final String SENSOR_RESOURCE = "sensor/";

    private CommunicationChannel channel;

    void init(){
        channel = new MockDeviceKnowledgeChannel();
    }

    @LINK
    public void findDeviceKnowledge(final String model, final OpFeedbackParam<DeviceKnowledgeResponse> knowledge){
        final CompletableFuture<ChannelResponse> future = channel.get(SENSOR_RESOURCE + model);
        try {
            final ChannelResponse response = future.get();
            if(response.getData().isPresent()){
                knowledge.set(DeviceKnowledgeResponse.successfulResponse(
                        getApplicationContext(),
                        new DeviceKnowledge(response.getData().get())
                ));
            } else {
                knowledge.set(DeviceKnowledgeResponse.failedResponse(getApplicationContext(), response.getCode()));
            }
        } catch (final ExecutionException | InterruptedException | JSONException e) {
            Log.e(TAG, "Error in findDeviceKnowledge: " + e.getLocalizedMessage());
            knowledge.set(DeviceKnowledgeResponse.applicationErrorResponse(getApplicationContext()));
        }
    }

}
