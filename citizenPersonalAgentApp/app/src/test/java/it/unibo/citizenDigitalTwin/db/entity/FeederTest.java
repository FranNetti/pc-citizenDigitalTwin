package it.unibo.citizenDigitalTwin.db.entity;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FeederTest {

    private static final String IS_RESOURCE = "isResource";
    private static final String NAME = "name";
    private static final String NAME_VALUE = "nameV";
    private static final String URI = "uri";
    private static final String URI_VALUE = "uriV";

    @Test
    public void createsCorrectly() {
        try {
            new Feeder(createFeeder(true));
            new Feeder(createFeeder(false));
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void storesResourceFeederCorrectly() {
        try {
            final JSONObject object = createFeeder(true);
            final Feeder feeder = new Feeder(object);
            assertTrue(feeder.isResource());
            assertEquals(URI_VALUE, feeder.getUri());
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test
    public void storesNonResourceFeederCorrectly() {
        try {
            final JSONObject object = createFeeder(false);
            final Feeder feeder = new Feeder(object);
            assertFalse(feeder.isResource());
            assertEquals(NAME_VALUE, feeder.getName());
        } catch (final JSONException e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void throwsExceptionWithMissingData() throws JSONException {
        final JSONObject object = new JSONObject();
        new Feeder(object);
    }

    private JSONObject createFeeder(final boolean isResource) throws JSONException {
        final JSONObject val = new JSONObject()
                .put(IS_RESOURCE, isResource);
        return isResource ? val.put(URI, URI_VALUE) : val.put(NAME, NAME_VALUE);
    }

}
