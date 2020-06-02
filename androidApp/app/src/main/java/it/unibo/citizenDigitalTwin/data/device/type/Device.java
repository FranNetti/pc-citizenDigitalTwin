package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcelable;

import java.util.Collection;
import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public interface Device extends Parcelable {
    String getName();
    List<LeafCategory> getCategories();
    void setCategories(Collection<LeafCategory> categories);
    CommunicationType getCommunicationType();
}
