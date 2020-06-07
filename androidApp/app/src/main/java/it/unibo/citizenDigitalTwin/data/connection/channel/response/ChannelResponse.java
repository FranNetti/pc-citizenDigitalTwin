package it.unibo.citizenDigitalTwin.data.connection.channel.response;

import org.json.JSONObject;

import java.util.Optional;

public class ChannelResponse extends Response {

    private final JSONObject data;
    private final String errorMessage;

    public static ChannelResponse createResponse(int code, JSONObject data, String errorMessage) {
        return new ChannelResponse(code,data,errorMessage);
    }

    public static ChannelResponse successfulResponse(int code, JSONObject data) {
        return createResponse(code,data,null);
    }

    public static ChannelResponse errorResponse(int code, String errorMessage) {
        return createResponse(code,null,errorMessage);
    }

    private ChannelResponse(int code, JSONObject data, String errorMessage) {
        super(code);
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public Optional<JSONObject> getData() {
        return Optional.ofNullable(data);
    }

    @Override
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
