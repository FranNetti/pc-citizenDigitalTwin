package it.unibo.citizenDigitalTwin.data.connection.channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

public class MockDeviceKnowledgeChannel implements CommunicationChannel {

    private static final String DEVICE_NAME = "sensor/braccialetto";

    @Override
    public CompletableFuture<ChannelResponse> patch(final String resource, final JSONObject data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<ChannelResponse> post(String resource, JSONObject data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<ChannelResponse> get(final String resource) {
        final CompletableFuture<ChannelResponse> future = new CompletableFuture<>();
        if(resource.equals(DEVICE_NAME)){
            final String message = "{ 'device': [ " +
                    createMockSensorResponse(LeafCategory.TEMPERATURE, "body/temperature", "°C", "get/body/temperature", "subscribe/body/temperature") +
                    ", " + createMockSensorResponse(LeafCategory.BLOOD_OXIGEN, "body/oxygen", "%", "get/body/oxygen", "subscribe/body/oxygen") +
                    ", " + createMockSensorResponse(LeafCategory.HEART_RATE, "body/heartrate", "bpm", "get/body/heartrate", "subscribe/body/heartrate") +
                    " ] }";
            try {
                future.complete(ChannelResponse.successfulResponse(HttpURLConnection.HTTP_OK, new JSONObject(message)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            future.complete(ChannelResponse.successfulResponse(HttpURLConnection.HTTP_NOT_FOUND, null));
        }
        return future;
    }

    @Override
    public CompletableFuture<Boolean> send(final String resource, final JSONObject data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subscribe(final Object subscriber, final String resource, final Consumer<JSONObject> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsubscribe(final Object subscriber, final String resource) {
        throw new UnsupportedOperationException();
    }

    private String createMockSensorResponse(
            final LeafCategory leafCategory,
            final String dataId,
            final String um,
            final String req,
            final String sub){
        return "{ " +
                "'leafCategory' : '" + leafCategory.getIdentifier() + "', " +
                "'dataId' : '" + dataId + "', " +
                "'um' : '" + um + "', " +
                "'requestMessage' : '" + req + "', " +
                "'subMessage' : '" + sub + "'" +
                " }";
    }
}
