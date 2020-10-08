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

    @Query("UPDATE wednesday_table SET status = -1")
    void resetStatus();

    @Query("DELETE FROM wednesday_table where subjectName = :subjectName")
    void deleteSubject(String subjectName);

    @Query("SELECT * FROM wednesday_table")
    LiveData<List<SubjectMinimal>> getAllSubjects();

    @Query("SELECT * FROM wednesday_table ORDER BY id")
    List<SubjectMinimal> getSubjectList();
}
