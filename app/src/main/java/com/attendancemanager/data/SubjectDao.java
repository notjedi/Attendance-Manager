package com.attendancemanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.attendancemanager.data.Subject;

import java.util.List;

@Dao
public interface SubjectDao {

    @Insert
    void insert(Subject subject);

    @Update
    void update(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("DELETE FROM subject_details_table")
    void deleteAllSubjects();

    @Query("SELECT * FROM subject_details_table ORDER BY id DESC")
    LiveData<List<Subject>> getAllSubjects();
}
