package it.unibo.citizenDigitalTwin.artifact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

import androidx.core.app.ActivityCompat;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that wraps the GPS sensor of the mobile phone.
 * @obsProperty data: the new position data produced by the sensor
 */
public class GPSArtifact extends JaCaArtifact {

    private static final String PROP_POSITION = "data";
    private static final String GPS_FEEDER = "GPS";

    private static final int REQ_CODE = 11;
    private static final int INTERVAL_FREQ = 2 * 60000;
    private static final int FASTEST_INTERVAL_FREQ = 60000;

    private FusedLocationProviderClient providerClient;
    private LocationRequest request;
    private LocationCallback callback;
    private Feeder feeder;
    private boolean isListeningForLocationUpdates;

    void init() {
        askForPermissionToUser();

        providerClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        request = LocationRequest.create();
        request.setInterval(INTERVAL_FREQ);
        request.setFastestInterval(FASTEST_INTERVAL_FREQ);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        isListeningForLocationUpdates = false;

        feeder = new Feeder(GPS_FEEDER);

        callback = new LocationCallback(){
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if(Objects.nonNull(locationResult)){
                    locationResult.getLocations()
                            .forEach(GPSArtifact.this::updateOrCreateLocationProperty);
                }
            }
        };
    }

    /**
     * Update the @obsProperty(data) with the last known position of the device.
     * @param success if the user has granted the location permission
     */
    @SuppressLint("MissingPermission")
    @OPERATION
    public void updatePosition(final OpFeedbackParam<Boolean> success){
        final Activity activity = getActivity(MainUIArtifact.MAIN_UI_ACTIVITY_NAME);
        if (isLocationPermissionGranted()) {
            askForPermissionToUser();
            success.set(false);
        } else if(Objects.nonNull(activity)){
            providerClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if(Objects.nonNull(location)){
                    updateOrCreateLocationProperty(location);
                }
            });
            success.set(true);
        }
    }

    /**
     * Subscribe the artifact for location updates.
     * @param success if the user has granted the location permission
     */
    @SuppressLint("MissingPermission")
    @OPERATION
    public void subscribeForLocationUpdates(final OpFeedbackParam<Boolean> success){
        if (isLocationPermissionGranted()) {
            askForPermissionToUser();
            success.set(false);
        } else if(!isListeningForLocationUpdates) {
            providerClient.requestLocationUpdates(request, callback, Looper.getMainLooper());
            isListeningForLocationUpdates = true;
            success.set(true);
        }
    }

    /**
     * Unsubscribe the artifact from location updates.
     */
    @OPERATION
    public void unsubscribeFromLocationUpdates(){
        if(isListeningForLocationUpdates) {
            providerClient.removeLocationUpdates(callback);
        }
    }

    private void askForPermissionToUser(){
        final Activity activity = getActivity(MainUIArtifact.MAIN_UI_ACTIVITY_NAME);
        if(Objects.nonNull(activity)){
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_CODE
            );
        }
    }

    private void updateOrCreateLocationProperty(final Location location){
        beginExternalSession();
        final Data data = createDataFromLocation(location);
        if(hasObsProperty(PROP_POSITION)) {
            updateObsProperty(PROP_POSITION, data);
        } else {
            defineObsProperty(PROP_POSITION, data);
        }
        endExternalSession(true);
    }

    private Data createDataFromLocation(final Location location){
        return new DataBuilder()
                .feeder(feeder)
                .leafCategory(LeafCategory.POSITION)
                .addInformation(
                        CommunicationStandard.LATITUDE,
                        location.getLatitude() + ""
                )
                .addInformation(
                        CommunicationStandard.LONGITUDE,
                        location.getLongitude() + ""
                )
                .build();
    }

    private boolean isLocationPermissionGranted(){
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

}
