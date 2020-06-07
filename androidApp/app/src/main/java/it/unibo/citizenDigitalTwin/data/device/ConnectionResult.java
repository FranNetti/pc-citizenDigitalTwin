package it.unibo.citizenDigitalTwin.data.device;

import android.content.Context;

import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.DeviceKnowledgeResponse;

public enum ConnectionResult {

    SUCCESS(R.string.connection_success),
    FAILURE(R.string.connection_failed),
    UNHANDLED_DEVICE(R.string.unhandled_device);

    public static ConnectionResult getFromDeviceKnowledgeResponse(final DeviceKnowledgeResponse response){
        switch (response.getCode()){
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
