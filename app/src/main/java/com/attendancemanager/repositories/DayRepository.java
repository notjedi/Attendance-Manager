package com.attendancemanager.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.attendancemanager.model.Friday;
import com.attendancemanager.model.FridayDao;
import com.attendancemanager.model.Monday;
import com.attendancemanager.model.MondayDao;
import com.attendancemanager.model.Saturday;
import com.attendancemanager.model.SaturdayDao;
import com.attendancemanager.model.SubjectDataBase;
import com.attendancemanager.model.Sunday;
import com.attendancemanager.model.SundayDao;
import com.attendancemanager.model.Thursday;
import com.attendancemanager.model.ThursdayDao;
import com.attendancemanager.model.Tuesday;
import com.attendancemanager.model.TuesdayDao;
import com.attendancemanager.model.Wednesday;
import com.attendancemanager.model.WednesdayDao;

import java.util.List;

public class DayRepository {

    public static final int INSERT = 0;
    public static final int DELETE = 1;
    public static final int DELETE_ALL_SUBJECTS = 2;

    private static DayRepository instance;

    private SubjectDataBase dataBase;
    private MondayDao mondayDao;
    private TuesdayDao tuesdayDao;
    private WednesdayDao wednesdayDao;
    private ThursdayDao thursdayDao;
    private FridayDao fridayDao;
    private SaturdayDao saturdayDao;
    private SundayDao sundayDao;

    public static DayRepository getInstance(Application application) {
        if (instance == null) {
            instance = new DayRepository(application);
        }
        return instance;
    }

    public DayRepository(Application application) {

        dataBase = SubjectDataBase.getInstance(application);
        mondayDao = dataBase.mondayDao();
        tuesdayDao = dataBase.tuesdayDao();
        wednesdayDao = dataBase.wednesdayDao();
        thursdayDao = dataBase.thursdayDao();
        fridayDao = dataBase.fridayDao();
        saturdayDao = dataBase.saturdayDao();
        sundayDao = dataBase.sundayDao();
    }

    public void insert(Object object) {
        runOperation(object, INSERT);
    }

    public void delete(Object object) {
        runOperation(object, DELETE);
    }

    public void deleteAllSubjects() {
        runOperation(null, DELETE_ALL_SUBJECTS);
    }

    private void runOperation(Object object, int operation) {
        /* https://stackoverflow.com/questions/50946488/java-instanceof-vs-class-name-switch-performance */

        if (object instanceof Monday)
            new Thread(new MondayOperations(mondayDao, ((Monday) object), operation)).start();
        else if (object instanceof Tuesday)
            new Thread(new TuesdayOperations(tuesdayDao, ((Tuesday) object), operation)).start();
        else if (object instanceof Wednesday)
            new Thread(new WednesdayOperations(wednesdayDao, ((Wednesday) object), operation)).start();
        else if (object instanceof Thursday)
            new Thread(new ThursdayOperations(thursdayDao, ((Thursday) object), operation)).start();
        else if (object instanceof Friday)
            new Thread(new FridayOperations(fridayDao, ((Friday) object), operation)).start();
        else if (object instanceof Saturday)
            new Thread(new SaturdayOperations(saturdayDao, ((Saturday) object), operation)).start();
        else if (object instanceof Sunday)
            new Thread(new SundayOperations(sundayDao, ((Sunday) object), operation)).start();
    }

    public LiveData<List<Monday>> getMondayList() { return mondayDao.getAllSubjects(); }

    public LiveData<List<Tuesday>> getTuesdayList() { return tuesdayDao.getAllSubjects(); }

    public LiveData<List<Wednesday>> getWednesdayList() { return wednesdayDao.getAllSubjects(); }

    public LiveData<List<Thursday>> getThursdayList() { return thursdayDao.getAllSubjects(); }

    public LiveData<List<Friday>> getFridayList() { return fridayDao.getAllSubjects(); }

    public LiveData<List<Saturday>> getSaturdayList() { return saturdayDao.getAllSubjects(); }

    public LiveData<List<Sunday>> getSundayList() { return sundayDao.getAllSubjects(); }

    public void closeDB() {
        if (dataBase.isOpen())
            dataBase.close();
    }

    private static class MondayOperations implements Runnable {

        private int operation;
        private MondayDao mondayDao;
        private Monday monday;

        public MondayOperations(@NonNull MondayDao mondayDao, @Nullable Monday monday, int operation) {
            this.operation = operation;
            this.mondayDao = mondayDao;
            this.monday = monday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    mondayDao.insert(monday);
                    break;
                case DELETE:
                    mondayDao.delete(monday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    mondayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class TuesdayOperations implements Runnable {

        private int operation;
        private TuesdayDao tuesdayDao;
        private Tuesday tuesday;

        public TuesdayOperations(@NonNull TuesdayDao tuesdayDao, @Nullable Tuesday tuesday, int operation) {
            this.operation = operation;
            this.tuesdayDao = tuesdayDao;
            this.tuesday = tuesday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    tuesdayDao.insert(tuesday);
                    break;
                case DELETE:
                    tuesdayDao.delete(tuesday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    tuesdayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class WednesdayOperations implements Runnable {

        private int operation;
        private WednesdayDao wednesdayDao;
        private Wednesday wednesday;

        public WednesdayOperations(@NonNull WednesdayDao wednesdayDao, @Nullable Wednesday wednesday, int operation) {
            this.operation = operation;
            this.wednesdayDao = wednesdayDao;
            this.wednesday = wednesday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    wednesdayDao.insert(wednesday);
                    break;
                case DELETE:
                    wednesdayDao.delete(wednesday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    wednesdayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class ThursdayOperations implements Runnable {

        private int operation;
        private ThursdayDao thursdayDao;
        private Thursday thursday;

        public ThursdayOperations(@NonNull ThursdayDao thursdayDao, @Nullable Thursday thursday, int operation) {
            this.operation = operation;
            this.thursdayDao = thursdayDao;
            this.thursday = thursday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    thursdayDao.insert(thursday);
                    break;
                case DELETE:
                    thursdayDao.delete(thursday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    thursdayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class FridayOperations implements Runnable {

        private int operation;
        private FridayDao fridayDao;
        private Friday friday;

        public FridayOperations(@NonNull FridayDao fridayDao, @Nullable Friday friday, int operation) {
            this.operation = operation;
            this.fridayDao = fridayDao;
            this.friday = friday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    fridayDao.insert(friday);
                    break;
                case DELETE:
                    fridayDao.delete(friday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    fridayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class SaturdayOperations implements Runnable {

        private int operation;
        private SaturdayDao saturdayDao;
        private Saturday saturday;

        public SaturdayOperations(@NonNull SaturdayDao saturdayDao, @Nullable Saturday saturday, int operation) {
            this.operation = operation;
            this.saturdayDao = saturdayDao;
            this.saturday = saturday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    saturdayDao.insert(saturday);
                    break;
                case DELETE:
                    saturdayDao.delete(saturday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    saturdayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }

    private static class SundayOperations implements Runnable {

        private int operation;
        private SundayDao sundayDao;
        private Sunday sunday;

        public SundayOperations(@NonNull SundayDao sundayDao, @Nullable Sunday sunday, int operation) {
            this.operation = operation;
            this.sundayDao = sundayDao;
            this.sunday = sunday;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    sundayDao.insert(sunday);
                    break;
                case DELETE:
                    sundayDao.delete(sunday);
                    break;
                case DELETE_ALL_SUBJECTS:
                    sundayDao.deleteAllSubjects();
                    break;
                default:
                    break;
            }
        }
    }
}
