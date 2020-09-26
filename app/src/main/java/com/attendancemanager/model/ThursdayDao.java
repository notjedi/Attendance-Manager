package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ThursdayDao {

    @Insert
    void insert(Thursday thursday);

    @Update
    void update(Thursday thursday);

    @Delete
    void delete(Thursday thursday);

    @Query("DELETE FROM thursday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM thursday_table")
    LiveData<List<Thursday>> getAllSubjects();
}