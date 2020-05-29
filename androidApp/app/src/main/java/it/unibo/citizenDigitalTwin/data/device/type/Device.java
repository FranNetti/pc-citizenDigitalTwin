package it.unibo.citizenDigitalTwin.data.device.type;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public interface Device {
    String getName();
    CommunicationType getCommunicationType();
    List<LeafCategory> getCategories();
}
