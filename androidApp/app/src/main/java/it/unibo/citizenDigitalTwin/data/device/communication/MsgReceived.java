package it.unibo.citizenDigitalTwin.data.device.communication;

import org.json.JSONException;
import org.json.JSONObject;

public class MsgReceived {

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String IS_PRESENT = "isPresent";

    private final String type;
    private final String value;
    private final boolean isPresent;

    public MsgReceived(final String message) throws JSONException{
        final JSONObject jsonMessage = new JSONObject(message);
        this.type = jsonMessage.getString(TYPE);
        this.value = jsonMessage.getString(VALUE);
        this.isPresent = jsonMessage.getBoolean(IS_PRESENT);
    }

    public String getType() {
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

    public boolean isPresent() {
        return this.isPresent;
    }
}
