package it.unibo.citizenDigitalTwin.data.connection;

import org.json.JSONObject;

public interface JsonSerializable {
    JSONObject toJson();
}
