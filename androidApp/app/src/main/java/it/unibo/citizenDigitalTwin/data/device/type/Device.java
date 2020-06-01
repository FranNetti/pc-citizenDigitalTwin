package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcelable;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public interface Device extends Parcelable {
    String getName();
    CommunicationType getCommunicationType();
    List<LeafCategory> getCategories();
}
