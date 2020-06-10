package it.unibo.citizenDigitalTwin.data.connection;

import android.content.Context;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public enum CommunicationStandard {

    DEFAULT_VALUE_IDENTIFIER("val", R.string.value, CommunicationStandard::singleValueToObject),
    DEFAULT_UNIT_OF_MEASURE_IDENTIFIER("um", R.string.um),
    LATITUDE("lat", R.string.lat, CommunicationStandard::positionDataToDouble),
    LONGITUDE("lng", R.string.lng, CommunicationStandard::positionDataToDouble);

    private final String identifier;
    private final int displayName;
    private final BiFunction<LeafCategory, String, Object> inferType;

    CommunicationStandard(final String identifier, final int displayName, final BiFunction<LeafCategory, String, Object> inferType){
        this.identifier = identifier;
        this.displayName = displayName;
        this.inferType = inferType;
    }

    CommunicationStandard(final String identifier, final int displayName){
        this(identifier, displayName, (leaf,value) -> value);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName(final Context context) {
        return context.getString(displayName);
    }

    public Object getInferType(final LeafCategory category, final String value) {
        return this.inferType.apply(category,value);
    }

    public static Optional<CommunicationStandard> findByIdentifier(final String identifier) {
        return Stream.of(CommunicationStandard.values())
                .filter(v -> v.identifier.equals(identifier))
                .findFirst();
    }

    private static Object positionDataToDouble(final LeafCategory category, final String value) {
        return category == LeafCategory.POSITION ? Double.parseDouble(value) : value;
    }

    private static Object singleValueToObject(final LeafCategory category, final String value) {
        switch(category) {
            case HEART_RATE:
            case BLOOD_OXIGEN:
                return Integer.parseInt(value);
            case TEMPERATURE:
                return Double.parseDouble(value);
            default:
                return value;
        }
    }
}
