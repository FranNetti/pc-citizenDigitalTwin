package it.unibo.citizenDigitalTwin.data.category;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Optional;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;

public enum GroupCategory {

    PERSONAL_DATA("personalData", R.string.GC_personal_data, R.drawable.ic_personal_black_24dp),
    MEDICAL_DATA("medicalData", R.string.GC_medical_data, R.drawable.ic_favorite_black_24dp),
    LOCATION("location", R.string.GC_location, R.drawable.ic_location_on_black_24dp),
    LEGAL_DATA("legalData", R.string.GC_legal_data, R.drawable.ic_legal_black_24dp),
    LICENCES("licences", R.string.GC_licences, R.drawable.ic_car_black_24dp);

    private final String identifier;
    private final int displayName;
    private final int displayIcon;

    GroupCategory(final String identifier, final int displayName, final int displayIcon){
        this.identifier = identifier;
        this.displayName = displayName;
        this.displayIcon = displayIcon;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public Drawable getDisplayIcon(final Context context) {
        return context.getDrawable(displayIcon);
    }

    public String getIdentifier() {
        return identifier;
    }

    public static Optional<GroupCategory> findByGroupIdentifier(final String identifier) {
        return Stream.of(GroupCategory.values())
                .filter(dataCategory -> dataCategory.identifier.equals(identifier))
                .findFirst();
    }
}
