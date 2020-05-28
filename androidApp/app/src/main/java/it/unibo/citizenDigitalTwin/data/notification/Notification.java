package it.unibo.citizenDigitalTwin.data.notification;

import java.io.Serializable;

public class Notification implements Serializable {

    public enum Type {
        DATA, MESSAGE;
    }

    private final Type type;
    private final String sender;
    private boolean selected;
    private boolean read;

    Notification(final Type type, final String sender){
        this.type = type;
        this.sender = sender;
        selected = false;
        read = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getSender() {
        return sender;
    }

    public Type getType() {
        return type;
    }
}
