package it.unibo.cop_medic.model.category;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * High level categories of the system
 */
public enum GroupCategory {

    PERSONAL_DATA("personalData"),
    MEDICAL_DATA("medicalData"),
    LOCATION("location"),
    LEGAL_DATA("legalData"),
    LICENCES("licences");

    private final String identifier;

    GroupCategory(final String identifier){
        this.identifier = identifier;
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
