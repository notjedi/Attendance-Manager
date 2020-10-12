package com.attendancemanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.attendancemanager.model.AppDatabase;
import com.attendancemanager.model.TimeTableDao;
import com.attendancemanager.model.TimeTableSubject;

import java.util.List;

public class DayRepository {

    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
    public static final int RESET_STATUS = 3;
    public static final int DELETE_ALL_SUBJECTS = 4;

    private static DayRepository instance;

    private AppDatabase dataBase;
    private TimeTableDao timeTableDao;

    public DayRepository(Application application) {

        dataBase = AppDatabase.getInstance(application);
        timeTableDao = dataBase.timeTableDao();
    }

    public static DayRepository getInstance(Application application) {
        if (instance == null) {
            instance = new DayRepository(application);
        }
        return instance;
    }

    public void insert(TimeTableSubject timeTableSubject) {
        timeTableDao.insert(timeTableSubject);
    }

    public void update(TimeTableSubject timeTableSubject) {
        timeTableDao.update(timeTableSubject);
    }

    public void delete(TimeTableSubject timeTableSubject) {
        timeTableDao.delete(timeTableSubject);
    }

    public void resetStatus(String day) {
        timeTableDao.resetStatus(day);
    }

    public void deleteSubjectByName(String subjectName) {
        timeTableDao.deleteSubjectByName(subjectName);
    }

    public void deleteAllSubjects() {
        timeTableDao.deleteAllSubjects();
    }

    public LiveData<List<TimeTableSubject>> getSubjectsOfDayLiveData(String day) {
        return timeTableDao.getSubjectsOfDayLiveData(day.toLowerCase());
    }

    public LiveData<List<TimeTableSubject>> getSubjectsOfDayWithoutTemp(String day) {
        return timeTableDao.getSubjectsOfDayWithoutTemp(day.toLowerCase());
    }

    public List<TimeTableSubject> getSubjectsOfDay(String day) {
        return timeTableDao.getSubjectsOfDay(day.toLowerCase());
    }

    public void deleteTempSubjects() {
        timeTableDao.deleteTempSubjects();
    }

    public void closeDB() {
        if (dataBase.isOpen())
            dataBase.close();
    }
}
