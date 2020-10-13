package com.attendancemanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.attendancemanager.model.AppDatabase;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.SubjectDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubjectRepository {

    private static SubjectRepository instance;

    private final SubjectDao subjectDao;
    private final LiveData<List<Subject>> allSubjects;

    public SubjectRepository(Application application) {

        AppDatabase dataBase = AppDatabase.getInstance(application);
        subjectDao = dataBase.subjectDao();
        allSubjects = subjectDao.getAllSubjects();
    }

    public static SubjectRepository getInstance(Application application) {
        if (instance == null) {
            instance = new SubjectRepository(application);
        }
        return instance;
    }

    public void insertSubject(Subject subject) {
        new Thread(new InsertSubjectRunnable(subjectDao, subject)).start();
    }

    public void deleteSubject(Subject subject) {
        new Thread(new DeleteSubjectRunnable(subjectDao, subject)).start();
    }

    public void updateSubject(Subject subject) {
        new Thread(new UpdateSubjectRunnable(subjectDao, subject)).start();
    }

    public boolean containsSubject(String subjectName) {
        /* Executing method in background and using
        Callable<Subject> instead of Runnable to get a return value */
        try {
            Callable<Subject> callable = () -> subjectDao.getSubject(subjectName);
            Future<Subject> future = Executors.newSingleThreadExecutor().submit(callable);
            return future.get() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Subject getSubject(String subjectName) {
        /* Executing method in background thread */
        /* https://stackoverflow.com/a/51720501 */
        try {
            Callable<Subject> callable = () -> subjectDao.getSubject(subjectName);
            Future<Subject> future = Executors.newSingleThreadExecutor().submit(callable);
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteAllSubjects() {
        new Thread(new DeleteAllSubjectRunnable(subjectDao)).start();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
    }

    private static class InsertSubjectRunnable implements Runnable {

        private final SubjectDao subjectDao;
        private final Subject subject;

        InsertSubjectRunnable(SubjectDao subjectDao, Subject subject) {
            this.subjectDao = subjectDao;
            this.subject = subject;
        }

        @Override
        public void run() {
            subjectDao.insert(subject);
        }
    }

    private static class DeleteSubjectRunnable implements Runnable {

        private final SubjectDao subjectDao;
        private final Subject subject;

        DeleteSubjectRunnable(SubjectDao subjectDao, Subject subject) {
            this.subjectDao = subjectDao;
            this.subject = subject;
        }

        @Override
        public void run() {
            subjectDao.delete(subject);
        }
    }

    private static class UpdateSubjectRunnable implements Runnable {

        private final SubjectDao subjectDao;
        private final Subject subject;

        UpdateSubjectRunnable(SubjectDao subjectDao, Subject subject) {
            this.subjectDao = subjectDao;
            this.subject = subject;
        }

        @Override
        public void run() {
            subjectDao.update(subject);
        }
    }

    private static class DeleteAllSubjectRunnable implements Runnable {

        private final SubjectDao subjectDao;

        DeleteAllSubjectRunnable(SubjectDao subjectDao) {
            this.subjectDao = subjectDao;
        }

        @Override
        public void run() {
            subjectDao.deleteAllSubjects();
        }
    }
}