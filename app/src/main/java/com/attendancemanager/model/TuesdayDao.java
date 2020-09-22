package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TuesdayDao {

    @Insert
    void insert(Tuesday tuesday);

    @Update
    void update(Tuesday tuesday);

    @Delete
    void delete(Tuesday tuesday);

    @Query("DELETE FROM tuesday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM tuesday_table")
    LiveData<List<Subject>> getAllSubjects();
}
