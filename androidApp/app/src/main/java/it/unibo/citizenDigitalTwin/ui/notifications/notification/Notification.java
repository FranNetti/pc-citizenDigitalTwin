package it.unibo.citizenDigitalTwin.ui.notifications.notification;

public class Notification {

    public enum Type {
        DATA, MESSAGE;
    }

    private final Type type;
    private final String sender;
    private boolean selected;

    Notification(final Type type, final String sender){
        this.type = type;
        this.sender = sender;
        selected = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getSender() {
        return sender;
    }

    public Type getType() {
        return type;
    }
}
