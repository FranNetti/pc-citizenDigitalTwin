package it.unibo.citizenDigitalTwin.commons;

import android.content.Context;

import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;

public enum LeafCategory {

    NAME("name", R.string.LC_name),
    SURNAME("surname", R.string.LC_surname),
    BIRTHDATE("birthdate", R.string.LC_birthdate),
    ADDRESS("address", R.string.LC_address),
    CELSIUS_TEMPERATURE("celsius_temperature",R.string.LC_celsius_temperature),
    BLOOD_OXIGEN("blood_oxigen", R.string.LC_blood_oxigen),
    HEART_RATE("heart_rate", R.string.LC_heart_rate);

    private final String name;
    private final int displayName;

    LeafCategory(final String name, final int displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public static Optional<LeafCategory> findByName(final String name) {
        return Stream.of(LeafCategory.values())
                .filter(dataCategory -> dataCategory.name.equals(name))
                .findFirst();
    }
}
