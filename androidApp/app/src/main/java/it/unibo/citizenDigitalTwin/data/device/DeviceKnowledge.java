package it.unibo.citizenDigitalTwin.data.device;

import java.util.Map;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

/**
 * Class that contains which sensors the device has and how to communicate with them
 */
public class DeviceKnowledge {

    private final Map<LeafCategory, SensorKnowledge> knowledge;

    public DeviceKnowledge(final Map<LeafCategory, SensorKnowledge> knowledge){
        this.knowledge = knowledge;
    }

    /**
     * Returns a map where at each leafCategory there is a sensor knowledge
     * @return a map where at each leafCategory there is a sensor knowledge
     */
    public Map<LeafCategory, SensorKnowledge> getKnowledge() {
        return knowledge;
    }
}
