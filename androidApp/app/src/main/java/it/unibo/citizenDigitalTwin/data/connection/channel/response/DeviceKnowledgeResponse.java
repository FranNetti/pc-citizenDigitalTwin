package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import java.net.HttpURLConnection;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.device.DeviceKnowledge;

public class DeviceKnowledgeResponse extends Response {

    public static DeviceKnowledgeResponse successfulResponse(final DeviceKnowledge knowledge){
        return new DeviceKnowledgeResponse(knowledge, HttpURLConnection.HTTP_OK);
    }

    public static DeviceKnowledgeResponse failedResponse(final int failCode){
        return new DeviceKnowledgeResponse(null, failCode);
    }

    public static DeviceKnowledgeResponse applicationErrorResponse(){
        return new DeviceKnowledgeResponse(null, APPLICATION_ERROR);
    }

    public static final int APPLICATION_ERROR = 600;

    private final DeviceKnowledge knowledge;

    private DeviceKnowledgeResponse(final DeviceKnowledge knowledge, final int failCode){
        super(failCode);
        this.knowledge = knowledge;
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.empty();
    }

    public Optional<DeviceKnowledge> getKnowledge() {
        return Optional.ofNullable(knowledge);
    }
}
