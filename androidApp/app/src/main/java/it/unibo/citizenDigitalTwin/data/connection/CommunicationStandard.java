package it.unibo.citizenDigitalTwin.data.connection;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static final String SEPARATOR = ",";

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
        switch (category) {
            case HEART_RATE:
            case BLOOD_OXIGEN:
                return Integer.parseInt(value);
            case TEMPERATURE:
                return Double.parseDouble(value);
            case MEDICAL_RECORD:
                final JSONArray array = new JSONArray();
                Stream.of(value.split(SEPARATOR)).map(String::trim).forEach(array::put);
                return array;
            default:
                return value;
        }
    }

    public static Pair<CommunicationStandard,String> decodeValue(final JSONObject json, final String name) throws JSONException, IllegalArgumentException {
        final CommunicationStandard communicationStandard = CommunicationStandard
                .findByIdentifier(name)
                .orElseThrow(() -> new IllegalArgumentException("Communication Standard not found: " + name));
        return Pair.create(communicationStandard,json.getString(name));
    }

    public static Pair<CommunicationStandard,String> decodeValue(final JSONArray json) throws JSONException {
        final StringBuilder value = new StringBuilder();
        final int length = json.length();
        if (length > 0)
            value.append(json.get(0));
        for (int i = 1; i < length; i++) {
            value.append(SEPARATOR).append(" ").append(json.get(i));
        }
        return Pair.create(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER,value.toString());
    }
}
