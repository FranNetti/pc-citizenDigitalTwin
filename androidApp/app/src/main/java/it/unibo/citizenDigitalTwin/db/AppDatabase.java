package it.unibo.citizenDigitalTwin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

@Database(entities = {Data.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "cdt_state";

    public abstract DataDAO dataDao();

}
