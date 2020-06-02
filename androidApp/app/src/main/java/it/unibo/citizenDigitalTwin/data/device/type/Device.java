package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcelable;

import java.util.Collection;
import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

/**
 * Interfaces for classes that represent an external device
 */
public interface Device extends Parcelable {
    /**
     * Getter
     * @return the name of the device
     */
    String getName();

    /**
     * Getter
     * @return which type of LeafCategories this device is capable of measuring
     */
    List<LeafCategory> getCategories();

    /**
     * Setter
     * @param categories the LeafCategories
     */
    void setCategories(Collection<LeafCategory> categories);

    /**
     * Getter
     * @return which type of communication to use with the device
     */
    CommunicationType getCommunicationType();
}
