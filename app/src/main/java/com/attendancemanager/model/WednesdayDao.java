package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WednesdayDao {

    @Insert
    void insert(Wednesday wednesday);

    @Update
    void update(Wednesday wednesday);

    @Delete
    void delete(Wednesday wednesday);

    @Query("DELETE FROM wednesday_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM wednesday_table")
    LiveData<List<SubjectMinimal>> getAllSubjects();

    @Query("SELECT * FROM wednesday_table")
    List<SubjectMinimal> getSubjectList();
}
