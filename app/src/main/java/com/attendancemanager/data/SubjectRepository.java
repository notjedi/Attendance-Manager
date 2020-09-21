package com.attendancemanager.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SubjectRepository {

    private SubjectDao subjectDao;
    private LiveData<List<Subject>> allSubjects;
    private SubjectDataBase dataBase;

    public SubjectRepository(Application application) {

        dataBase = SubjectDataBase.getInstance(application);
        subjectDao = dataBase.subjectDao();
        allSubjects = subjectDao.getAllSubjects();
    }

    public void insert(Subject subject) {
        new Thread(new InsertSubjectRunnable(subjectDao, subject)).start();
    }

    public void delete(Subject subject) {
        new Thread(new DeleteSubjectRunnable(subjectDao, subject)).start();
    }

    public void update(Subject subject) {
        new Thread(new UpdateSubjectRunnable(subjectDao, subject)).start();
    }

    public void deleteAllSubjects() {
        new Thread(new DeleteAllSubjectRunnable(subjectDao)).start();
    }

    public void closeDB() {
        if (dataBase.isOpen())
            dataBase.close();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
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
