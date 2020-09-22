package com.attendancemanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MondayDao {

    @Insert
    void insert(Monday monday);

    @Update
    void update(Monday monday);

    @Delete
    void delete(Monday monday);

    @Query("DELETE FROM monday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM monday_table")
    LiveData<List<Subject>> getAllSubjects();
}
