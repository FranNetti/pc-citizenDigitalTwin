package it.unibo.citizenDigitalTwin.commons;

import android.content.Context;

import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;

public enum GroupCategory {

    PERSONAL_DATA("personalData", R.string.GC_personal_data),
    MEDICAL_DATA("medicalData", R.string.GC_medical_data),
    LOCATION("location", R.string.GC_location),
    LEGAL_DATA("legalData", R.string.GC_legal_data),
    LICENCES("licences", R.string.GC_licences);

    private final String identifier;
    private final int displayName;

    GroupCategory(final String identifier, final int displayName){
        this.identifier = identifier;
        this.displayName = displayName;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public String getIdentifier() {
        return identifier;
    }

    public static Optional<GroupCategory> findByName(final String name) {
        return Stream.of(GroupCategory.values())
                .filter(dataCategory -> dataCategory.identifier.equals(name))
                .findFirst();
    }
}
