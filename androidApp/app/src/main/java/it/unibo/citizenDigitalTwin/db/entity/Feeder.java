package it.unibo.citizenDigitalTwin.db.entity;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

public class Feeder implements Serializable {

    @ColumnInfo(name = "feeder_isResource") private boolean isResource;
    @ColumnInfo(name = "feeder_uri") private String uri;
    @ColumnInfo(name = "feeder_name") private String name;

    public Feeder(final boolean isResource, final String uri, final String name) {
        this.isResource = isResource;
        this.uri = uri;
        this.name = name;
    }

    @Ignore
    public Feeder(final String uri, final String name) {
        this(true,uri,name);
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
}
