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

    @Query("UPDATE thursday_table SET status = -1")
    void resetStatus();

    @Query("DELETE FROM thursday_table WHERE subjectName = :subjectName")
    void deleteSubject(String subjectName);

    @Query("DELETE FROM thursday_table WHERE id IN (SELECT id FROM thursday_table ORDER BY id DESC limit :limit)")
    void deleteLimited(int limit);

    @Query("SELECT * FROM thursday_table")
    LiveData<List<SubjectMinimal>> getAllSubjects();

    @Query("SELECT * FROM thursday_table ORDER BY id")
    List<SubjectMinimal> getSubjectList();
}
