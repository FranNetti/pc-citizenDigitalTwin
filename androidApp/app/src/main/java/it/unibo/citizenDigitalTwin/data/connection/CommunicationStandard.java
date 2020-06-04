package it.unibo.citizenDigitalTwin.data.connection;

import android.content.Context;

import it.unibo.citizenDigitalTwin.R;

public enum CommunicationStandard {

    DEFAULT_VALUE_IDENTIFIER("value", R.string.value),
    DEFAULT_UNIT_OF_MEASURE_IDENTIFIER("um", R.string.um),
    LATITUDE("lat", R.string.lat),
    LONGITUDE("lng", R.string.lng);

    private final String message;
    private final int displayName;

    CommunicationStandard(final String message, final int displayName){
        this.message = message;
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }
}
