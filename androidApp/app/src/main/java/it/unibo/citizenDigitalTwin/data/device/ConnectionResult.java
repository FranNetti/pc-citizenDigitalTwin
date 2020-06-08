package it.unibo.citizenDigitalTwin.data.device;

import android.content.Context;

import java.net.HttpURLConnection;

import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.DeviceKnowledgeResponse;

public enum ConnectionResult {

    SUCCESS(R.string.connection_success),
    FAILURE(R.string.connection_failed),
    UNHANDLED_DEVICE(R.string.unhandled_device),
    SERVER_ERROR(R.string.model_search_failed);

    public static ConnectionResult getFromDeviceKnowledgeResponse(final DeviceKnowledgeResponse response){
        switch (response.getCode()){
            case HttpURLConnection.HTTP_OK: return SUCCESS;
            case HttpURLConnection.HTTP_NOT_FOUND: return UNHANDLED_DEVICE;
            case HttpURLConnection.HTTP_INTERNAL_ERROR: return SERVER_ERROR;
            default: return FAILURE;
        }
    }

    private final int code;

    ConnectionResult(final int code){
        this.code = code;
    }

    public String getMessage(final Context context) {
        return context.getString(code);
    }

}
