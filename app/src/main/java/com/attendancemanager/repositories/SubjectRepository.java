package com.attendancemanager.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.attendancemanager.model.Subject;
import com.attendancemanager.model.SubjectDao;
import com.attendancemanager.model.SubjectDataBase;

import java.util.List;

public class SubjectRepository {

    private static SubjectRepository instance;

    private SubjectDao subjectDao;
    private LiveData<List<Subject>> allSubjects;
    private SubjectDataBase dataBase;

    public static SubjectRepository getInstance(Application application) {
        if (instance == null) {
            instance = new SubjectRepository(application);
        }
        return instance;
    }

    public SubjectRepository(Application application) {

        dataBase = SubjectDataBase.getInstance(application);
        subjectDao = dataBase.subjectDao();
        allSubjects = subjectDao.getAllSubjects();
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

    public void deleteAllSubjects() {
        new Thread(new DeleteAllSubjectRunnable(subjectDao)).start();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
    }

    public Subject containsSubject(String subjectName) {
        return subjectDao.containsSubject(subjectName);
    }

    public Subject getSubject(String subjectName) {
        return subjectDao.getSubject(subjectName);
    }

    public void closeDB() {
        if (dataBase.isOpen())
            dataBase.close();
    }

    private static class InsertSubjectRunnable implements Runnable {

        private SubjectDao subjectDao;
        private Subject subject;

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

        private SubjectDao subjectDao;
        private Subject subject;

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

        private SubjectDao subjectDao;
        private Subject subject;

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

        private SubjectDao subjectDao;

        DeleteAllSubjectRunnable(SubjectDao subjectDao) {
            this.subjectDao = subjectDao;
        }

        @Override
        public void run() {
            subjectDao.deleteAllSubjects();
        }
    }
}