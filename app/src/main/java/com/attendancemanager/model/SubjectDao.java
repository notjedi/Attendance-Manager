package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SubjectDao {

    @Insert
    void insert(Subject subject);

    @Update
    void update(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("DELETE FROM subject_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM subject_table WHERE subjectName = :subjectName")
    Subject getSubject(String subjectName);

    @Query("SELECT * FROM subject_table")
    LiveData<List<Subject>> getAllSubjects();
}
