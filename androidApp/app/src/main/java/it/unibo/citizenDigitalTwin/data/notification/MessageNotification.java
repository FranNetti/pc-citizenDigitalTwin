package it.unibo.citizenDigitalTwin.data.notification;

public class MessageNotification extends Notification {

    private final String message;

    public MessageNotification(final String sender, final String message) {
        super(Type.MESSAGE, sender);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
