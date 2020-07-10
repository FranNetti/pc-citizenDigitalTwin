package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import org.json.JSONObject;

import java.util.Optional;

/**
 * Class that represents a response obtained from a channel.
 */
public class ChannelResponse extends Response {

    private final JSONObject data;
    private final String errorMessage;

    /**
     * Create a new response with the given code, data and error message.
     * @param code the code
     * @param data the received data
     * @param errorMessage the error message
     * @return a new ChannelResponse instance
     */
    public static ChannelResponse createResponse(int code, JSONObject data, String errorMessage) {
        return new ChannelResponse(code,data,errorMessage);
    }

    /**
     * Create a new successful response.
     * @param code the code
     * @param data the received data
     * @return a new ChannelResponse instance
     */
    public static ChannelResponse successfulResponse(int code, JSONObject data) {
        return createResponse(code,data,null);
    }

    /**
     * Create a new error response.
     * @param code the code
     * @param errorMessage the error message
     * @return a new ChannelResponse instance
     */
    public static ChannelResponse errorResponse(int code, String errorMessage) {
        return createResponse(code,null,errorMessage);
    }

    private ChannelResponse(int code, JSONObject data, String errorMessage) {
        super(code);
        this.data = data;
        this.errorMessage = errorMessage;
    }

    /**
     * Get the data if present.
     * @return the data
     */
    public Optional<JSONObject> getData() {
        return Optional.ofNullable(data);
    }

    /**
     * Get the error if present.
     * @return the error
     */
    @Override
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
