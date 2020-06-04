package it.unibo.citizenDigitalTwin.data.category;

import android.content.Context;

import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;

import static it.unibo.citizenDigitalTwin.data.category.GroupCategory.*;

/**
 * Low level categories of the system
 */
public enum LeafCategory {

    NAME(PERSONAL_DATA, "name", R.string.LC_name),
    SURNAME(PERSONAL_DATA, "surname", R.string.LC_surname),
    BIRTHDATE(PERSONAL_DATA,"birthdate", R.string.LC_birthdate),
    ADDRESS(PERSONAL_DATA, "address", R.string.LC_address),
    CF(PERSONAL_DATA, "cf", R.string.LC_cf),
    /* ----------------------------------------------- */
    MEDICAL_RECORD(MEDICAL_DATA, "medicalRecord", R.string.LC_medical_record),
    HEART_RATE(MEDICAL_DATA, "heartrate", R.string.LC_heart_rate),
    TEMPERATURE(MEDICAL_DATA, "bodyTemperature",R.string.LC_celsius_temperature),
    BLOOD_OXIGEN(MEDICAL_DATA, "spo2", R.string.LC_blood_oxigen),
    /* ----------------------------------------------- */
    POSITION(LOCATION, "position", R.string.LC_position);

    private final GroupCategory groupCategory;
    private final String identifier;
    private final int displayName;

    LeafCategory(final GroupCategory category, final String identifier, final int displayName) {
        this.groupCategory = category;
        this.identifier = identifier;
        this.displayName = displayName;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public GroupCategory getGroupCategory() {
        return groupCategory;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public static Optional<LeafCategory> findByLeafIdentifier(final String identifier) {
        return Stream.of(LeafCategory.values())
                .filter(dataCategory -> dataCategory.identifier.equals(identifier))
                .findFirst();
    }
}
