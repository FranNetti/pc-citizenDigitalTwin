package it.unibo.citizenDigitalTwin.ui.notifications.notification;

public class Notification {

    public enum Type {
        DATA, MESSAGE;
    }

    private final Type type;
    private final String sender;

    public Notification(final Type type, final String sender){
        this.type = type;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public Type getType() {
        return type;
    }
}
