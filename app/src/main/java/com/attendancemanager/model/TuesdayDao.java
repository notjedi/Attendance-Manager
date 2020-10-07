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

    @Query("UPDATE tuesday_table SET status = -1")
    void resetStatus();

    @Query("SELECT * FROM tuesday_table")
    LiveData<List<SubjectMinimal>> getAllSubjects();

    @Query("SELECT * FROM tuesday_table ORDER BY id")
    List<SubjectMinimal> getSubjectList();
}
