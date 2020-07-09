package it.unibo.citizenDigitalTwin.data.device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains which sensors the device has and how to communicate with them.
 */
public class DeviceKnowledge {

    private static final String DEVICE = "device";

    private final List<SensorKnowledge> sensorKnowledgeList;

    public DeviceKnowledge(final JSONObject object) throws JSONException, IllegalStateException {
        sensorKnowledgeList = new ArrayList<>();
        final JSONArray sensors = object.getJSONArray(DEVICE);
        final int length = sensors.length();
        if(length <= 0){
            throw new IllegalStateException("No sensor knowledge found");
        }
        for(int x = 0; x < length; x++){
            sensorKnowledgeList.add(new SensorKnowledge(sensors.getJSONObject(x)));
        }
    }

    /**
     * Returns a list containing all the information required to communicate with each sensor of the device.
     * @return a list containing all the information required to communicate with each sensor of the device
     */
    public List<SensorKnowledge> getSensorKnowledge() {
        return sensorKnowledgeList;
    }
}
