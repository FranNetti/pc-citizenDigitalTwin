package it.unibo.citizenDigitalTwin.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

@Dao
public interface DataDAO {

    @Query("SELECT * FROM state")
    LiveData<List<Data>> getAll();

    @Query("SELECT * FROM state WHERE leafCategory LIKE :leafCategoryName LIMIT 1")
    LiveData<Data> getDataFromCategory(String leafCategoryName);

    @Insert
    void insert(Data data);

    @Insert
    void insertAll(Data... data);

    @Query("DELETE FROM state")
    void clear();

    @Delete
    void delete(Data data);

    @Update
    void update(Data data);

}
