package it.unibo.citizenDigitalTwin.data.connection;

import org.json.JSONObject;

import java.util.Optional;

public class ChannelResponse {
    private final int code;
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
        this.code = code;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public Optional<JSONObject> getData() {
        return Optional.ofNullable(data);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
