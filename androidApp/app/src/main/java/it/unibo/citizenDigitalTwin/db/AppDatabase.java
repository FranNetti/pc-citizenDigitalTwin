package it.unibo.citizenDigitalTwin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

@Database(entities = {Data.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DataDAO dataDao();

}
