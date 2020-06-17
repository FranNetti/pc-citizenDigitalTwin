package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import java.net.HttpURLConnection;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.device.DeviceKnowledge;

/**
 * Class that contains a response regarding a device knowledge.
 */
public class DeviceKnowledgeResponse extends Response {

    /**
     * Create a successful device knowledge response.
     * @param knowledge the device knowledge
     * @return a new instance of DeviceKnowledgeResponse
     */
    public static DeviceKnowledgeResponse successfulResponse(final DeviceKnowledge knowledge){
        return new DeviceKnowledgeResponse(knowledge, HttpURLConnection.HTTP_OK);
    }

    /**
     * Create a failed device knowledge response.
     * @param failCode the error code
     * @return a new instance of DeviceKnowledgeResponse
     */
    public static DeviceKnowledgeResponse failedResponse(final int failCode){
        return new DeviceKnowledgeResponse(null, failCode);
    }

    /**
     * Create a device knowledge response that indicated an application error.
     * @return a new instance of DeviceKnowledgeResponse
     */
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

    /**
     * Get the device knowledge if present.
     * @return the knowledge
     */
    public Optional<DeviceKnowledge> getKnowledge() {
        return Optional.ofNullable(knowledge);
    }
}
