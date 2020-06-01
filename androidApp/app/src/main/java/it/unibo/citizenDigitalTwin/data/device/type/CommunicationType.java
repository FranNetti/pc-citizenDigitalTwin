package it.unibo.citizenDigitalTwin.data.device.type;

import android.content.Context;
import android.graphics.drawable.Drawable;

import it.unibo.citizenDigitalTwin.R;

public enum CommunicationType {

    BT(R.drawable.ic_baseline_bluetooth_24),
    BLE(R.drawable.ic_baseline_bluetooth_24),
    WIFI(R.drawable.ic_baseline_wifi_24);

    private final int displayIcon;

    CommunicationType(final int displayIcon){
        this.displayIcon = displayIcon;
    }

    public Drawable getDisplayIcon(final Context context) {
        return context.getDrawable(displayIcon);
    }
}
