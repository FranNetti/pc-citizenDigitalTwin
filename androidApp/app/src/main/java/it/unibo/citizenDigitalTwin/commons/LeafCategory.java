package it.unibo.citizenDigitalTwin.commons;

import java.util.Optional;
import java.util.stream.Stream;

public enum LeafCategory {

    NAME("name", "Nome"),
    SURNAME("surname", "Cognome"),
    BIRTHDATE("birthdate", "Data di nascita"),
    ADDRESS("address", "Indirizzo"),
    CELSIUS_TEMPERATURE("celsius_temperature","Temperatura corporea");

    private final String name;
    private final String displayName;

    LeafCategory(final String name, final String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<LeafCategory> findByName(final String name) {
        return Stream.of(LeafCategory.values())
                .filter(dataCategory -> dataCategory.name.equals(name))
                .findFirst();
    }
}
