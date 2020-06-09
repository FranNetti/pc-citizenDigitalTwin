package it.unibo.citizenDigitalTwin.data.connection;

import android.content.Context;

import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;

public enum CommunicationStandard {

    DEFAULT_VALUE_IDENTIFIER("val", R.string.value),
    DEFAULT_UNIT_OF_MEASURE_IDENTIFIER("um", R.string.um),
    LATITUDE("lat", R.string.lat),
    LONGITUDE("lng", R.string.lng);

    private final String identifier;
    private final int displayName;

    CommunicationStandard(final String identifier, final int displayName){
        this.identifier = identifier;
        this.displayName = displayName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public static Optional<CommunicationStandard> findByIdentifier(final String identifier) {
        return Stream.of(CommunicationStandard.values())
                .filter(v -> v.identifier.equals(identifier))
                .findFirst();
    }
}
