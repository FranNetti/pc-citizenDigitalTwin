package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcelable;

import java.util.Collection;
import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

/**
 * Interfaces for classes that represent an external device.
 */
public interface Device extends Parcelable {
    /**
     * Name getter.
     * @return the name of the device
     */
    String getName();

    /**
     * Categories getter.
     * @return which type of LeafCategories this device is capable of measuring
     */
    List<LeafCategory> getCategories();

    /**
     * Categories setter.
     * @param categories the LeafCategories
     */
    void setCategories(Collection<LeafCategory> categories);

    /**
     * CommunicationType getter.
     * @return which type of communication to use with the device
     */
    CommunicationType getCommunicationType();
}
