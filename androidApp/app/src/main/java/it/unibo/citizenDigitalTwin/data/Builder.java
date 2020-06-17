package it.unibo.citizenDigitalTwin.data;

/**
 * Interface for builders.
 * @param <K>
 */
public interface Builder<K> {
    /**
     * Build the object.
     * @return the object
     */
    K build();
}
