package com.effseele.effilearn.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT questionid FROM savedoption where id=:id " )
    String getQstnID(String id);

    @Query("SELECT answerid FROM savedoption where questionid=:questionid" )
    String getAnsID(String questionid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedOption task);
//
    @Update
    void update(SavedOption task);
//
    @Query("delete from savedoption")
    void deleteAll();

//
//    @Delete
//    void delete(SavedOption task);
//
//    @Update
//    void update(SavedOption task);
//
//    @Query("delete from savedoption")
//    void deleteAll();

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMultipleMovies (Services arrayListist);

}


