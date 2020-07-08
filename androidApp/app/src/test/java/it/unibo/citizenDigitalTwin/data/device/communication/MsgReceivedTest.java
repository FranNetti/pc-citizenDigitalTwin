package it.unibo.citizenDigitalTwin.data.device.communication;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MsgReceivedTest {

    private static final String TYPE = "type";
    private static final String TYPE_VALUE = "typeValue";
    private static final String VALUE = "value";
    private static final String VALUE_VALUE = "valueValue";
    private static final String IS_PRESENT = "isPresent";
    private static final boolean IS_PRESENT_VALUE = true;

    private JSONObject createJSONObject() throws JSONException {
        return new JSONObject()
                .put(TYPE, TYPE_VALUE)
                .put(VALUE, VALUE_VALUE)
                .put(IS_PRESENT, IS_PRESENT_VALUE);
    }

    @Test
    public void parseJSONDataCorrectly() {
        try {
            final JSONObject object = createJSONObject();
            new MsgReceived(object.toString());
        } catch (final JSONException e) {
            System.err.println(e.getLocalizedMessage());
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void throwsExceptionWithWrongData() throws JSONException {
        final JSONObject object;
        try {
             object = new JSONObject()
                    .put(TYPE, TYPE_VALUE)
                    .put(VALUE, VALUE_VALUE);
        } catch (final JSONException e) {
            fail();
            return;
        }
        new MsgReceived(object.toString());
    }

    @Test
    public void storeDataCorrectly() {
        try {
            final JSONObject object = createJSONObject();
            final MsgReceived msg = new MsgReceived(object.toString());
            assertEquals(TYPE_VALUE, msg.getType());
            assertEquals(VALUE_VALUE, msg.getValue());
            assertEquals(IS_PRESENT_VALUE, msg.isPresent());
        } catch (final JSONException e) {
            System.err.println(e.getLocalizedMessage());
            fail();
        }
    }

}
