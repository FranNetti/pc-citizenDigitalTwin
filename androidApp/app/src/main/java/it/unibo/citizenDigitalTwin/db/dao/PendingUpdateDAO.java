package it.unibo.citizenDigitalTwin.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import it.unibo.citizenDigitalTwin.db.entity.PendingUpdate;

@Dao
public interface PendingUpdateDAO {

    @Query("SELECT * FROM pendingUpdates")
    Flowable<List<PendingUpdate>> getAll();

    @Insert()
    void insert(PendingUpdate pendingUpdate);

    @Delete
    void delete(PendingUpdate pendingUpdate);

    @Query("DELETE FROM pendingUpdates WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM pendingUpdates")
    void clear();

    @Update
    void update(PendingUpdate pendingUpdate);
}
