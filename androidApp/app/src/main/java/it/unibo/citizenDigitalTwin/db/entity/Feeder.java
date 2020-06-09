package it.unibo.citizenDigitalTwin.db.entity;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

import it.unibo.citizenDigitalTwin.data.connection.JsonSerializable;

public class Feeder implements Serializable, JsonSerializable {

    private static final String IS_RESOURCE = "isResource";
    private static final String NAME = "name";
    private static final String URI = "uri";

    @ColumnInfo(name = "feeder_isResource") private boolean isResource;
    @ColumnInfo(name = "feeder_uri", defaultValue = "") private String uri;
    @ColumnInfo(name = "feeder_name", defaultValue = "") private String name;

    public Feeder(final boolean isResource, final String uri, final String name) {
        this.isResource = isResource;
        this.uri = uri;
        this.name = name;
    }

    @Ignore
    public Feeder(final String uri, final String name) {
        this(true,uri,name);
    }

    @Ignore
    public Feeder(final JSONObject json) throws JSONException {
        this.isResource = json.getBoolean(IS_RESOURCE);
        if (isResource)
            this.uri = json.getString(URI);
        else
            this.name = json.getString(NAME);
    }

    public boolean isResource() {
        return isResource;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResource(boolean resource) {
        isResource = resource;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(NAME,name)
                .put(IS_RESOURCE,isResource)
                .put(URI,uri);
    }
}
