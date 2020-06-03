package it.unibo.citizenDigitalTwin.db.dao;

import java.util.Collection;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Flowable;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

@Dao
public interface DataDAO {

    @Query("SELECT * FROM state")
    Flowable<List<Data>> getAll();

    @Query("SELECT * FROM state WHERE leafCategory LIKE :leafCategoryName LIMIT 1")
    Flowable<Data> getDataFromCategory(String leafCategoryName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Data data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Collection<Data> data);

    @Query("DELETE FROM state")
    void clear();

    @Delete
    void delete(Data data);

    @Update
    void update(Data data);

}
