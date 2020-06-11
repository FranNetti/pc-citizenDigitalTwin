package it.unibo.citizenDigitalTwin.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;

@Entity(tableName = "pendingUpdates")
public class PendingUpdate {

    @PrimaryKey private long id;
    private JSONArray data;

    public PendingUpdate(final long id, final JSONArray data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public JSONArray getData() {
        return data;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
