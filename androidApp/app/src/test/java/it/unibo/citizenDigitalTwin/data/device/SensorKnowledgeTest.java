package it.unibo.citizenDigitalTwin.data.device;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Optional;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class SensorKnowledgeTest {

    private static final String LEAF_CATEGORY = "leafCategory";
    private static final LeafCategory LEAF_CATEGORY_VALUE = LeafCategory.TEMPERATURE;
    private static final String DATA_ID = "dataId";
    private static final String DATA_ID_VALUE = "dataId";
    private static final String UM = "um";
    private static final String UM_VALUE = "temperature";
    private static final String REQ_FOR_DATA = "requestMessage";
    private static final String REQ_FOR_DATA_VALUE = "reqTest";
    private static final String SUB_FOR_DATA = "subMessage";
    private static final String SUB_FOR_DATA_VALUE = "subTest";

    private static final String WRONG_TYPE = "wrongType";
    private static final String WRONG_VALUE = "wrongValue";

    @Test
    public void createsCorrectly() {
        try {
            final JSONObject object = createPartialKnowledge()
                    .put(REQ_FOR_DATA, REQ_FOR_DATA_VALUE)
                    .put(SUB_FOR_DATA, SUB_FOR_DATA_VALUE);
            new SensorKnowledge(object);
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void throwsExceptionWithMissingData() throws JSONException {
        final JSONObject object;
        try {
            object = new JSONObject()
                    .put(WRONG_TYPE, WRONG_VALUE);
        } catch (final JSONException e) {
            fail();
            return;
        }
        new SensorKnowledge(object);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsExceptionWithWrongLeafCategory() throws JSONException {
        final JSONObject object;
        try {
            object = new JSONObject()
                    .put(LEAF_CATEGORY, WRONG_VALUE);
        } catch (final JSONException e) {
            fail();
            return;
        }
        new SensorKnowledge(object);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsExceptionWithMissingReqOrSubMessage() throws JSONException {
        final JSONObject object;
        try {
            object = createPartialKnowledge();
        } catch (final JSONException e) {
            fail();
            return;
        }
        new SensorKnowledge(object);
    }

    @Test
    public void storesDateCorrectly() {
        try {
            final JSONObject object = createPartialKnowledge()
                    .put(REQ_FOR_DATA, REQ_FOR_DATA_VALUE)
                    .put(SUB_FOR_DATA, SUB_FOR_DATA_VALUE);

            final SensorKnowledge sensorKnowledge = new SensorKnowledge(object);
            assertEquals(sensorKnowledge.getReqDataMessage(), Optional.of(REQ_FOR_DATA_VALUE));
            assertEquals(sensorKnowledge.getSubForDataMessage(), Optional.of(SUB_FOR_DATA_VALUE));
            assertEquals(sensorKnowledge.getLeafCategory(), LEAF_CATEGORY_VALUE);
            assertEquals(sensorKnowledge.getSensorDataIdentifier(), DATA_ID_VALUE);
            assertEquals(sensorKnowledge.getUnitOfMeasure(), UM_VALUE);
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void subForDataMessageCanBeAbsent() {
        try {
            final JSONObject object = createPartialKnowledge()
                    .put(REQ_FOR_DATA, REQ_FOR_DATA_VALUE);

            final SensorKnowledge sensorKnowledge = new SensorKnowledge(object);
            assertFalse(sensorKnowledge.getSubForDataMessage().isPresent());
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void reqDataMessageCanBeAbsent() {
        try {
            final JSONObject object = createPartialKnowledge()
                    .put(SUB_FOR_DATA, SUB_FOR_DATA_VALUE);

            final SensorKnowledge sensorKnowledge = new SensorKnowledge(object);
            assertFalse(sensorKnowledge.getReqDataMessage().isPresent());
        } catch (final JSONException e) {
            fail();
        }
    }

    private JSONObject createPartialKnowledge() throws JSONException {
        return new JSONObject()
                .put(LEAF_CATEGORY, LEAF_CATEGORY_VALUE.getIdentifier())
                .put(DATA_ID, DATA_ID_VALUE)
                .put(UM, UM_VALUE);
    }

}
