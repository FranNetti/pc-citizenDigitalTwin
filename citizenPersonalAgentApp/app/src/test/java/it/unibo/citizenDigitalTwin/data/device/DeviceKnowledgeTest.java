package it.unibo.citizenDigitalTwin.data.device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DeviceKnowledgeTest {

    private static final String DEVICE = "device";
    private static final String LEAF_CATEGORY = "leafCategory";
    private static final LeafCategory LEAF_CATEGORY_VALUE = LeafCategory.TEMPERATURE;
    private static final String DATA_ID = "dataId";
    private static final String DATA_ID_VALUE = "dataId";
    private static final String UM = "um";
    private static final String UM_VALUE = "temperature";
    private static final String REQ_FOR_DATA = "requestMessage";
    private static final String REQ_FOR_DATA_VALUE = "reqTest";

    @Test(expected = JSONException.class)
    public void throwsExceptionWithWrongData() throws JSONException {
        final JSONObject object = new JSONObject();
        new DeviceKnowledge(object);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsExceptionWithEmptyArray() throws JSONException {
        final JSONObject object = new JSONObject()
                .put(DEVICE, new JSONArray());
        new DeviceKnowledge(object);
    }

    @Test
    public void createsCorrectly() {
        try {
            final JSONObject object = new JSONObject();
            final JSONArray array = new JSONArray();
            for(int i = 0; i < 3; i++){
                array.put(createJSONSensorKnowledge(
                        LEAF_CATEGORY_VALUE,
                        DATA_ID_VALUE,
                        UM_VALUE,
                        REQ_FOR_DATA_VALUE
                ));
            }
            object.put(DEVICE, array);
            new DeviceKnowledge(object);
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void storesDataCorrectly() {
        try {
            final JSONObject object = new JSONObject();
            final List<SensorKnowledge> knowledgeList = new ArrayList<>();
            final JSONArray array = new JSONArray();
            for(int i = 0; i < 3; i++){
                final SensorKnowledge knowledge = createSensorKnowledge(
                        DATA_ID_VALUE + i,
                        REQ_FOR_DATA_VALUE + i
                );
                knowledgeList.add(knowledge);
                array.put(createJSONSensorKnowledge(knowledge));
            }
            object.put(DEVICE, array);
            DeviceKnowledge result = new DeviceKnowledge(object);
            assertTrue(result.getSensorKnowledge().containsAll(knowledgeList));
        } catch (final JSONException e) {
            fail();
        }
    }

    private JSONObject createJSONSensorKnowledge(final SensorKnowledge knowledge) throws JSONException {
        return createJSONSensorKnowledge(
                knowledge.getLeafCategory(),
                knowledge.getSensorDataIdentifier(),
                knowledge.getUnitOfMeasure(),
                knowledge.getReqDataMessage().get()
        );
    }

    private JSONObject createJSONSensorKnowledge(final LeafCategory category, final String id, final String um, final String reqMessage) throws JSONException {
        return new JSONObject()
                .put(LEAF_CATEGORY, category.getIdentifier())
                .put(DATA_ID, id)
                .put(UM, um)
                .put(REQ_FOR_DATA, reqMessage);
    }

    private SensorKnowledge createSensorKnowledge(final String id, final String reqMessage) {
        return new SensorKnowledge.SensorKnowledgeBuilder()
                .leafCategory(DeviceKnowledgeTest.LEAF_CATEGORY_VALUE)
                .sensorDataIdentifier(id)
                .unitOfMeasure(DeviceKnowledgeTest.UM_VALUE)
                .reqDataMessage(reqMessage)
                .build();
    }

}
