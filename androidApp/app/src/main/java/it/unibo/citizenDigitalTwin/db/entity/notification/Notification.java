package it.unibo.citizenDigitalTwin.db.entity.notification;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class Notification implements Serializable {

    public enum Type {
        DATA, MESSAGE;
    }

    @Ignore                          private boolean selected;
    @Ignore                          private final Type type;
    @PrimaryKey(autoGenerate = true) private long id;
    @ColumnInfo                      private String sender;
    @ColumnInfo                      private boolean read;

    Notification(final Type type, final String sender){
        this.type = type;
        this.sender = sender;
        selected = false;
        read = false;
    }

    Notification(final Type type, final long id, final String sender, final boolean read){
        this(type, sender);
        this.id = id;
        this.read = read;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(final boolean read) {
        this.read = read;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public Type getType() {
        return type;
    }
}
