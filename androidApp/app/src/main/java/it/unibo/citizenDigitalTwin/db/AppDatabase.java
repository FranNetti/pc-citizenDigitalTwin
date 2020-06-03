package it.unibo.citizenDigitalTwin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

@Database(entities = {Data.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DataDAO dataDao();

}
