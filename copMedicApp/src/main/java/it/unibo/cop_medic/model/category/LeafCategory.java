package it.unibo.cop_medic.model.category;

import java.util.Optional;
import java.util.stream.Stream;

import static it.unibo.cop_medic.model.category.GroupCategory.*;

/**
 * Low level categories of the system
 */
public enum LeafCategory {

    NAME(PERSONAL_DATA, "name"),
    SURNAME(PERSONAL_DATA, "surname"),
    BIRTHDATE(PERSONAL_DATA,"birthdate"),
    ADDRESS(PERSONAL_DATA, "address"),
    CF(PERSONAL_DATA, "cf"),
    /* ----------------------------------------------- */
    MEDICAL_RECORD(MEDICAL_DATA, "medicalRecord"),
    HEART_RATE(MEDICAL_DATA, "heartrate"),
    TEMPERATURE(MEDICAL_DATA, "bodyTemperature"),
    BLOOD_OXIGEN(MEDICAL_DATA, "spo2"),
    /* ----------------------------------------------- */
    POSITION(LOCATION, "position");

    private final GroupCategory groupCategory;
    private final String identifier;

    LeafCategory(final GroupCategory category, final String identifier) {
        this.groupCategory = category;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public GroupCategory getGroupCategory() {
        return groupCategory;
    }

    public static Optional<LeafCategory> findByLeafIdentifier(final String identifier) {
        return Stream.of(LeafCategory.values())
                .filter(dataCategory -> dataCategory.identifier.equals(identifier))
                .findFirst();
    }
}
