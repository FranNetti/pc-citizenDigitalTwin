package it.unibo.citizenDigitalTwin.db.entity.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;

import static org.junit.Assert.assertEquals;

public class DataTest {

    private static final String IDENTIFIER = "id";
    private static final String FEEDER = "feeder";
    private static final String TIMESTAMP = "timestamp";
    private static final String DATA_CATEGORY = "category";
    private static final String VALUE = "value";

    private static final String FEEDER_NAME = "testName";
    private static final String MEDICAL_RECORD_VALUE = "testDisease";
    private static final String STRING_VALUE = "testValue";
    private static final int NUMERIC_VALUE = 3;

    @Test
    public void createsCorrectly() throws JSONException {
        for(final LeafCategory cat: LeafCategory.values()){
            new Data(createJSONObject(cat, true));
        }
    }

    @Test(expected = JSONException.class)
    public void throwsExceptionWithMissingData() throws JSONException {
        for(final LeafCategory cat: LeafCategory.values()){
            new Data(createJSONObject(cat, false));
        }
    }

    @Test
    public void parsesCorrectly() throws JSONException {
        final Date date = new Date();
        final JSONObject object = createBasicJSONData(date)
            .put(DATA_CATEGORY, LeafCategory.NAME.getIdentifier())
            .put(VALUE, STRING_VALUE);
        final Data data = new DataBuilder()
                .feeder(new Feeder(FEEDER_NAME))
                .date(date)
                .uri(IDENTIFIER)
                .leafCategory(LeafCategory.NAME)
                .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, STRING_VALUE)
                .build();
        assertEquals(object.toString(), data.toJson().toString());
    }

    @Test
    public void parsesCorrectlyFromJSON() throws JSONException {
        final JSONObject object = createJSONObject(LeafCategory.NAME, true);
        final Data data = new Data(object);
        assertEquals(object.toString(), data.toJson().toString());
    }

    private JSONObject createJSONObject(final LeafCategory category, final boolean hasToBeTheCorrectValue) throws JSONException {
        final JSONObject temp = createBasicJSONData().put(DATA_CATEGORY, category.getIdentifier());
        if (hasToBeTheCorrectValue) {
            switch (category) {
                case HEART_RATE:
                case BLOOD_OXIGEN:
                    return temp.put(VALUE, NUMERIC_VALUE + "");
                case TEMPERATURE:
                    return temp.put(VALUE, new JSONObject()
                            .put(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER.getIdentifier(), NUMERIC_VALUE + "")
                            .put(CommunicationStandard.DEFAULT_UNIT_OF_MEASURE_IDENTIFIER.getIdentifier(), STRING_VALUE)
                    );
                case MEDICAL_RECORD:
                    return temp.put(VALUE, new JSONArray().put(MEDICAL_RECORD_VALUE));
                case POSITION:
                    return temp.put(VALUE, new JSONObject()
                            .put(CommunicationStandard.LATITUDE.getIdentifier(), NUMERIC_VALUE + "")
                            .put(CommunicationStandard.LONGITUDE.getIdentifier(), NUMERIC_VALUE + "")
                    );
                default:
                    return temp.put(VALUE, STRING_VALUE);
            }
        } else {
            return temp;
        }
    }

    private JSONObject createBasicJSONData(final Date time) throws JSONException {
        return new JSONObject()
                .put(IDENTIFIER, IDENTIFIER)
                .put(TIMESTAMP, time.getTime())
                .put(FEEDER, new Feeder(FEEDER_NAME).toJson());
    }

    private JSONObject createBasicJSONData() throws JSONException {
        return createBasicJSONData(new Date());
    }

}
