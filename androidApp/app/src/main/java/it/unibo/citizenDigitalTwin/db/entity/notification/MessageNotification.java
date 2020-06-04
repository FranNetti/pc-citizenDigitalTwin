package it.unibo.citizenDigitalTwin.db.entity.notification;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "messageNotifications")
public class MessageNotification extends Notification {

    @ColumnInfo private String message;

    @Ignore
    public MessageNotification(final String sender, final String message) {
        super(Type.MESSAGE, sender);
        this.message = message;
    }

    public MessageNotification(final long id, final String sender, final String message, final boolean read){
        super(Type.MESSAGE, id, sender, read);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
