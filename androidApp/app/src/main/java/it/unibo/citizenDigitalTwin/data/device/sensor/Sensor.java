package it.unibo.citizenDigitalTwin.data.device.sensor;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public interface Sensor<X> {
    X getData();
    LeafCategory getLeafCategory();
}
