package com.attendancemanager.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimeTableDao {

    @Insert
    void insert(TimeTableSubject timeTableSubject);

    @Update
    void update(TimeTableSubject timeTableSubject);

    @Delete
    void delete(TimeTableSubject timeTableSubject);

    @Query("DELETE FROM time_table")
    void deleteAllSubjects();

    @Query("DELETE FROM time_table WHERE subjectName = :subjectName")
    void deleteSubjectByName(String subjectName);

    /* Deletes all the subjects where temp = true(extra class subjects) */
    @Query("DELETE FROM time_table WHERE `temp` = 1")
    void deleteTempSubjects();

    /* Resets the status to DayViewModel.NONE */
    @Query("UPDATE time_table SET status = -1 where day = :day")
    void resetStatus(String day);

    /* Returns all the subjects of the day irrespective of temp */
    @Query("SELECT * FROM time_table WHERE day = :day")
    List<TimeTableSubject> getSubjectsOfDay(String day);

    /* Returns LiveData of all subjects of the day */
    @Query("SELECT * FROM time_table WHERE day = :day")
    LiveData<List<TimeTableSubject>> getSubjectsOfDayLiveData(String day);

    /* Returns all the subjects which are not temporary(not extra class subjects) */
    @Query("SELECT * FROM time_table WHERE day = :day AND `temp` = 0")
    LiveData<List<TimeTableSubject>> getSubjectsOfDayWithoutTemp(String day);

    @Query("SELECT * FROM time_table")
    LiveData<List<TimeTableSubject>> getAllSubjects();
}
