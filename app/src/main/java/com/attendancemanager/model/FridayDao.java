package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FridayDao {

    @Insert
    void insert(Friday friday);

    @Update
    void update(Friday friday);

    @Delete
    void delete(Friday friday);

    @Query("DELETE FROM friday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM friday_table")
    LiveData<List<Friday>> getAllSubjects();
}
