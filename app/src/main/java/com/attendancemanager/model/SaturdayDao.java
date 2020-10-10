package com.attendancemanager.model;

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

    @Query("UPDATE saturday_table SET status = -1")
    void resetStatus();

    @Query("DELETE FROM saturday_table WHERE subjectName = :subjectName")
    void deleteSubject(String subjectName);

    @Query("DELETE FROM saturday_table WHERE id IN (SELECT id FROM saturday_table ORDER BY id DESC limit :limit)")
    void deleteLimited(int limit);

    @Query("SELECT * FROM saturday_table")
    LiveData<List<SubjectMinimal>> getAllSubjects();

    @Query("SELECT * FROM saturday_table ORDER BY id")
    List<SubjectMinimal> getSubjectList();
}
