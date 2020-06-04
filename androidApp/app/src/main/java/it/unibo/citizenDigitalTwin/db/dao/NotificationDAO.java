package it.unibo.citizenDigitalTwin.db.dao;

import java.util.Collection;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Flowable;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;

@Dao
public interface NotificationDAO {

    @Query("SELECT * FROM messageNotifications")
    Flowable<List<MessageNotification>> getAllMessageNotifications();

    @Query("SELECT * FROM dataNotification")
    Flowable<List<DataNotification>> getAllDataNotifications();

    @Insert
    void insertMessageNotification(MessageNotification notification);

    @Insert
    void insertDataNotification(DataNotification notification);

    @Update
    void updateMessageNotifications(Collection<MessageNotification> notifications);

    @Update
    void updateDataNotifications(Collection<DataNotification> notification);

}