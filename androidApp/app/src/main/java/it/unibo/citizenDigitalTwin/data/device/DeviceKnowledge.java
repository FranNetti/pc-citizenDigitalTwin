package it.unibo.citizenDigitalTwin.data.device;

import java.util.Map;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class DeviceKnowledge {

    private final Map<LeafCategory, SensorKnowledge> knowledge;

    public DeviceKnowledge(final Map<LeafCategory, SensorKnowledge> knowledge){
        this.knowledge = knowledge;
    }

    public Map<LeafCategory, SensorKnowledge> getKnowledge() {
        return knowledge;
    }
}
