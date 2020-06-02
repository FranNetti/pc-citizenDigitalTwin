package it.unibo.citizenDigitalTwin.data.device.communication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a new message received from a remote device
 */
public class MsgReceived {

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String IS_PRESENT = "isPresent";

    private final String type;
    private final String value;
    private final boolean isPresent;

    /**
     * Create a new MsgReceived from a JSON represented as a String
     * @param message the JSON message
     * @throws JSONException if the message is not in a JSON format
     */
    public MsgReceived(final String message) throws JSONException {
        final JSONObject jsonMessage = new JSONObject(message);
        this.type = jsonMessage.getString(TYPE);
        this.value = jsonMessage.getString(VALUE);
        this.isPresent = jsonMessage.getBoolean(IS_PRESENT);
    }

    /**
     * Getter
     * @return the field type of the message
     */
    public String getType() {
        return this.type;
    }

    /**
     * Getter
     * @return the field value of the message
     */
    public String getValue(){
        return this.value;
    }

    /**
     * Used to know if the value field is present
     * @return if the value field is present
     */
    public boolean isPresent() {
        return this.isPresent;
    }
}
