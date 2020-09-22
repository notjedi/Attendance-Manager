package com.attendancemanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SaturdayDao {

    @Insert
    void insert(Saturday saturday);

    @Update
    void update(Saturday saturday);

    @Delete
    void delete(Saturday saturday);

    @Query("DELETE FROM saturday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM saturday_table")
    LiveData<List<Subject>> getAllSubjects();
}
