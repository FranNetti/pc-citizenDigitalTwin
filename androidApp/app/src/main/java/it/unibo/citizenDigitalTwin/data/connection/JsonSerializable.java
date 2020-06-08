package it.unibo.citizenDigitalTwin.data.connection;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonSerializable {
    JSONObject toJson() throws JSONException;
}
