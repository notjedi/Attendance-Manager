package com.attendancemanager;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.attendancemanager.data.Subject;

import java.util.List;
import java.util.logging.Handler;

public class SubjectRepository {

    private SubjectDao subjectDao;
    private LiveData<List<Subject>> allSubjects;

    public SubjectRepository(Application application) {

        SubjectDataBase dataBase = SubjectDataBase.getInstance(application);
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
