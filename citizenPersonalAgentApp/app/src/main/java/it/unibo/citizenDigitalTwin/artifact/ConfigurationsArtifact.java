package it.unibo.citizenDigitalTwin.artifact;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

import it.unibo.citizenDigitalTwin.R;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

public class ConfigurationsArtifact extends JaCaArtifact {

    private static final String TAG = "[ConfigurationsArtifact]";
    private static final String AUTH_SERVICE_ADDRESS = "authentication_service";
    private static final String CITIZEN_SERVICE_ADDRESS = "citizen_service";
    private static final String PROP_AUTH_ADDRESS = "authenticationService";
    private static final String PROP_CITIZEN_ADDRESS = "citizenService";

    private static final String defaultAddress = "localhost";
    private static final int defaultAuthPort = 8080;
    private static final int defaultCitizenPort = 8081;

    void init() {
        final Resources resources = getApplicationContext().getResources();
        String authAddress;
        String citizenAddress;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(resources.openRawResource(R.raw.cdt_settings)));
            final Optional<String> jsonValue = reader.lines().reduce((a, b) -> a + " " + b);
            final JSONObject json = new JSONObject(jsonValue.get());
            authAddress = json.getString(AUTH_SERVICE_ADDRESS);
            citizenAddress = json.getString(CITIZEN_SERVICE_ADDRESS);
        } catch (final Exception e) {
            Log.e(TAG, "Exception in init: " + e.getLocalizedMessage());
            authAddress = defaultAddress + defaultAuthPort;
            citizenAddress = defaultAddress + defaultCitizenPort;
        }
        beginExternalSession();
        defineObsProperty(PROP_AUTH_ADDRESS, authAddress);
        defineObsProperty(PROP_CITIZEN_ADDRESS, citizenAddress);
        endExternalSession(true);
    }

}
