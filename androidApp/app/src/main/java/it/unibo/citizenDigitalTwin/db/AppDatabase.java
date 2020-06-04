package it.unibo.citizenDigitalTwin.db;

import android.content.Context;

import java.util.Objects;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.dao.NotificationDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;

@Database(
        entities = {
            Data.class,
            DataNotification.class,
            MessageNotification.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "cdt_state";
    private static AppDatabase INSTANCE = null;

    public static synchronized AppDatabase getInstance(final Context context){
        if(Objects.isNull(INSTANCE)){
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
        }
        return INSTANCE;
    }

    public abstract DataDAO dataDao();
    public abstract NotificationDAO notificationDAO();

}
