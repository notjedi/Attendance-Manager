package com.attendancemanager.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.attendancemanager.model.AppDatabase;
import com.attendancemanager.model.Friday;
import com.attendancemanager.model.FridayDao;
import com.attendancemanager.model.Monday;
import com.attendancemanager.model.MondayDao;
import com.attendancemanager.model.Saturday;
import com.attendancemanager.model.SaturdayDao;
import com.attendancemanager.model.SubjectMinimal;
import com.attendancemanager.model.Sunday;
import com.attendancemanager.model.SundayDao;
import com.attendancemanager.model.Thursday;
import com.attendancemanager.model.ThursdayDao;
import com.attendancemanager.model.Tuesday;
import com.attendancemanager.model.TuesdayDao;
import com.attendancemanager.model.Wednesday;
import com.attendancemanager.model.WednesdayDao;

import java.io.FileWriter;
import java.util.List;

public class DayRepository {

    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
    public static final int DELETE_ALL_SUBJECTS = 3;

    private static DayRepository instance;

    private AppDatabase dataBase;
    private MondayDao mondayDao;
    private TuesdayDao tuesdayDao;
    private WednesdayDao wednesdayDao;
    private ThursdayDao thursdayDao;
    private FridayDao fridayDao;
    private SaturdayDao saturdayDao;
    private SundayDao sundayDao;

    public DayRepository(Application application) {

        dataBase = AppDatabase.getInstance(application);
        mondayDao = dataBase.mondayDao();
        tuesdayDao = dataBase.tuesdayDao();
        wednesdayDao = dataBase.wednesdayDao();
        thursdayDao = dataBase.thursdayDao();
        fridayDao = dataBase.fridayDao();
        saturdayDao = dataBase.saturdayDao();
        sundayDao = dataBase.sundayDao();
    }

    public static DayRepository getInstance(Application application) {
        if (instance == null) {
            instance = new DayRepository(application);
        }
        return instance;
    }

    public void insert(SubjectMinimal subjectMinimal) {
        runOperation(subjectMinimal, INSERT);
    }

    public void update(SubjectMinimal subjectMinimal) {
        runOperation(subjectMinimal, UPDATE);
    }

    public void delete(SubjectMinimal subjectMinimal) {
        runOperation(subjectMinimal, DELETE);
    }

    public void deleteAllSubjects() {
        runOperation(null, DELETE_ALL_SUBJECTS);
    }

    private void runOperation(SubjectMinimal subjectMinimal, int operation) {
        /* https://stackoverflow.com/questions/50946488/java-instanceof-vs-class-name-switch-performance */
        /* TODO try to migrate parameters to a new SubjectMinimal class */

        if (operation == DELETE_ALL_SUBJECTS) {
            new Thread(new MondayOperations(mondayDao, null, operation)).start();
            new Thread(new TuesdayOperations(tuesdayDao, null, operation)).start();
            new Thread(new WednesdayOperations(wednesdayDao, null, operation)).start();
            new Thread(new ThursdayOperations(thursdayDao, null, operation)).start();
            new Thread(new FridayOperations(fridayDao, null, operation)).start();
            new Thread(new SaturdayOperations(saturdayDao, null, operation)).start();
            new Thread(new SundayOperations(sundayDao, null, operation)).start();
            return;
        }

        switch (subjectMinimal.getDay().toLowerCase()) {
            case "monday":
                new Thread(new MondayOperations(mondayDao, subjectMinimal, operation)).start();
                break;
            case "tuesday":
                new Thread(new TuesdayOperations(tuesdayDao, subjectMinimal, operation)).start();
                break;
            case "wednesday":
                new Thread(new WednesdayOperations(wednesdayDao, subjectMinimal, operation)).start();
                break;
            case "thursday":
                new Thread(new ThursdayOperations(thursdayDao, subjectMinimal, operation)).start();
                break;
            case "friday":
                new Thread(new FridayOperations(fridayDao, subjectMinimal, operation)).start();
                break;
            case "saturday":
                new Thread(new SaturdayOperations(saturdayDao, subjectMinimal, operation)).start();
                break;
            case "sunday":
                new Thread(new SundayOperations(sundayDao, subjectMinimal, operation)).start();
                break;
        }
    }

    public LiveData<List<SubjectMinimal>> getMondayList() {
        return mondayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getTuesdayList() {
        return tuesdayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getWednesdayList() {
        return wednesdayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getThursdayList() {
        return thursdayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getFridayList() {
        return fridayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getSaturdayList() {
        return saturdayDao.getAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getSundayList() {
        return sundayDao.getAllSubjects();
    }

    public List<SubjectMinimal> getSubjectList(String dayName) {
        switch (dayName.toLowerCase()) {
            case "monday":
                return mondayDao.getSubjectList();
            case "tuesday":
                return tuesdayDao.getSubjectList();
            case "wednesday":
                return wednesdayDao.getSubjectList();
            case "thursday":
                return thursdayDao.getSubjectList();
            case "friday":
                return fridayDao.getSubjectList();
            case "saturday":
                return saturdayDao.getSubjectList();
            case "sunday":
                return sundayDao.getSubjectList();
        }
        return null;
    }

    public void closeDB() {
        if (dataBase.isOpen())
            dataBase.close();
    }

    private static class MondayOperations implements Runnable {

        private int operation;
        private MondayDao mondayDao;
        private SubjectMinimal subjectMinimal;

        public MondayOperations(@NonNull MondayDao mondayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.mondayDao = mondayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            Monday monday;
            switch (operation) {
                case INSERT:
                    mondayDao.insert(new Monday(subjectMinimal.getSubjectName()));
                    break;
                case UPDATE:
                    monday = new Monday(subjectMinimal.getSubjectName());
                    monday.setId(subjectMinimal.getId());
                    mondayDao.update(monday);
                    break;
                case DELETE:
                    monday = new Monday(subjectMinimal.getSubjectName());
                    monday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public TuesdayOperations(@NonNull TuesdayDao tuesdayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.tuesdayDao = tuesdayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            Tuesday tuesday;
            switch (operation) {
                case INSERT:
                    tuesdayDao.insert(new Tuesday(subjectMinimal.getSubjectName()));
                    break;
                case UPDATE:
                    tuesday = new Tuesday(subjectMinimal.getSubjectName());
                    tuesday.setId(subjectMinimal.getId());
                    tuesdayDao.update(tuesday);
                    break;
                case DELETE:
                    tuesday = new Tuesday(subjectMinimal.getSubjectName());
                    tuesday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public WednesdayOperations(@NonNull WednesdayDao wednesdayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.wednesdayDao = wednesdayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    wednesdayDao.insert(new Wednesday(subjectMinimal.getSubjectName()));
                    break;
                case DELETE:
                    Wednesday wednesday = new Wednesday(subjectMinimal.getSubjectName());
                    wednesday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public ThursdayOperations(@NonNull ThursdayDao thursdayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.thursdayDao = thursdayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    thursdayDao.insert(new Thursday(subjectMinimal.getSubjectName()));
                    break;
                case DELETE:
                    Thursday thursday = new Thursday(subjectMinimal.getSubjectName());
                    thursday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public FridayOperations(@NonNull FridayDao fridayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.fridayDao = fridayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    fridayDao.insert(new Friday(subjectMinimal.getSubjectName()));
                    break;
                case DELETE:
                    Friday friday = new Friday(subjectMinimal.getSubjectName());
                    friday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public SaturdayOperations(@NonNull SaturdayDao saturdayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.saturdayDao = saturdayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    saturdayDao.insert(new Saturday(subjectMinimal.getSubjectName()));
                    break;
                case DELETE:
                    Saturday saturday = new Saturday(subjectMinimal.getSubjectName());
                    saturday.setId(subjectMinimal.getId());
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
        private SubjectMinimal subjectMinimal;

        public SundayOperations(@NonNull SundayDao sundayDao, @Nullable SubjectMinimal subjectMinimal, int operation) {
            this.operation = operation;
            this.sundayDao = sundayDao;
            this.subjectMinimal = subjectMinimal;
        }

        @Override
        public void run() {
            switch (operation) {
                case INSERT:
                    sundayDao.insert(new Sunday(subjectMinimal.getSubjectName()));
                    break;
                case DELETE:
                    Sunday sunday = new Sunday(subjectMinimal.getSubjectName());
                    sunday.setId(subjectMinimal.getId());
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
