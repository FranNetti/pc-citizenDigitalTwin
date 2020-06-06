package it.unibo.citizenDigitalTwin.data.device;

import android.content.Context;

import it.unibo.citizenDigitalTwin.R;

public enum ConnectionResult {

    SUCCESS(R.string.connection_success),
    FAILURE(R.string.connection_failed),
    UNHANDLED_DEVICE(R.string.unhandled_device);

    private final int code;

    ConnectionResult(final int code){
        this.code = code;
    }

    public String getMessage(final Context context) {
        return context.getString(code);
    }

}
