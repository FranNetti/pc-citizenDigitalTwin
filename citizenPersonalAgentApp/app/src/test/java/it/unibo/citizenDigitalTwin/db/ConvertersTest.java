package it.unibo.citizenDigitalTwin.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConvertersTest {

    private static final String VALUE_1 = "value";
    private static final String VALUE_2 = "value2";
    private static final String LIST_LEAF_TAG = "leaves";
    private static final CommunicationStandard COMM_VALUE_1 = CommunicationStandard.DEFAULT_UNIT_OF_MEASURE_IDENTIFIER;
    private static final CommunicationStandard COMM_VALUE_2 = CommunicationStandard.DEFAULT_VALUE_IDENTIFIER;
    private static final List<LeafCategory> LEAF_LIST = Arrays.asList(LeafCategory.NAME, LeafCategory.SURNAME);

    @Test
    public void convertsFromJSONStringToMap(){

        try {
            final JSONObject object = createCommJSONObject();
            final Map<CommunicationStandard, String> map = createCommunicationMap();
            final Map<CommunicationStandard, String> result = Converters.fromStringToMap(object.toString());
            assertEquals(map, result);
        } catch (JSONException e) {
            fail();
        }
    }

    @Test
    public void convertsFromMapToJSONString(){
        try {
            final JSONObject object = createCommJSONObject();
            final Map<CommunicationStandard, String> map = createCommunicationMap();
            final String result = Converters.fromMapToString(map);
            assertEquals(object.toString(), result);
        } catch (JSONException e) {
            fail();
        }
    }

    @Test
    public void convertsFromJSONStringToList(){
        try {
            final List<LeafCategory> result = Converters.fromStringToList(createLeafJSONObject().toString());
            assertEquals(LEAF_LIST, result);
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void convertsFromListToJSONString(){
        try {
            final String result = Converters.fromListToString(LEAF_LIST);
            assertEquals(createLeafJSONObject().toString(), result);
        } catch (final JSONException e) {
            fail();
        }
    }

    private JSONObject createCommJSONObject() throws JSONException {
        return new JSONObject()
                .put(COMM_VALUE_1.name(), VALUE_1)
                .put(COMM_VALUE_2.name(), VALUE_2);
    }

    private Map<CommunicationStandard, String> createCommunicationMap(){
        final Map<CommunicationStandard, String> map = new HashMap<>();
        map.put(COMM_VALUE_1, VALUE_1);
        map.put(COMM_VALUE_2, VALUE_2);
        return map;
    }

    private JSONObject createLeafJSONObject() throws JSONException {
        final JSONObject object = new JSONObject();
        final JSONArray array = new JSONArray();
        LEAF_LIST.forEach(x -> array.put(x.name()));
        return object.put(LIST_LEAF_TAG, array);
    }

}
