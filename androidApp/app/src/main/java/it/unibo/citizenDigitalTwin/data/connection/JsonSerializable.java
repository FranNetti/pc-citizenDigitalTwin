package it.unibo.citizenDigitalTwin.data.connection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for classes that can be transformed to a JSONObject.
 */
public interface JsonSerializable {

    /**
     * Create a new JSONObject from the current class values.
     * @return a new JSONObject
     * @throws JSONException if a parse error occurs
     */
    JSONObject toJson() throws JSONException;
}
