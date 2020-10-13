package com.attendancemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.attendancemanager.model.Subject;
import com.attendancemanager.repositories.SubjectRepository;

import java.util.List;

public class SubjectViewModel extends AndroidViewModel {

    private final SubjectRepository subjectRepository;
    private final LiveData<List<Subject>> allSubjects;

    public SubjectViewModel(@NonNull Application application) {
        super(application);

        subjectRepository = SubjectRepository.getInstance(application);
        allSubjects = subjectRepository.getAllSubjects();
    }

    public void insert(Subject subject) {
        subjectRepository.insertSubject(subject);
    }

    public void delete(Subject subject) {
        subjectRepository.deleteSubject(subject);
    }

    public void update(Subject subject) {
        subjectRepository.updateSubject(subject);
    }

    public boolean containsSubject(String subjectName) {
        return subjectRepository.containsSubject(subjectName);
    }

    public Subject getSubject(String subjectName) {
        return subjectRepository.getSubject(subjectName);
    }

    public void deleteAllSubjects() {
        subjectRepository.deleteAllSubjects();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
    }

}
