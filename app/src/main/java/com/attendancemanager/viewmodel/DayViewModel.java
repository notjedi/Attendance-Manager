package com.attendancemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.attendancemanager.model.SubjectMinimal;
import com.attendancemanager.repositories.DayRepository;

import java.util.List;

public class DayViewModel extends AndroidViewModel {

    public static final int NONE = -1;
    public static final int CANCELLED = 0;
    public static final int BUNKED = 1;
    public static final int ATTENDED = 2;

    private DayRepository dayRepository;
    private LiveData<List<SubjectMinimal>> mondayList;
    private LiveData<List<SubjectMinimal>> tuesdayList;
    private LiveData<List<SubjectMinimal>> wednesdayList;
    private LiveData<List<SubjectMinimal>> thursdayList;
    private LiveData<List<SubjectMinimal>> fridayList;
    private LiveData<List<SubjectMinimal>> saturdayList;
    private LiveData<List<SubjectMinimal>> sundayList;


    public DayViewModel(@NonNull Application application) {
        super(application);

        dayRepository = DayRepository.getInstance(application);
        mondayList = dayRepository.getMondayList();
        tuesdayList = dayRepository.getTuesdayList();
        wednesdayList = dayRepository.getWednesdayList();
        thursdayList = dayRepository.getThursdayList();
        fridayList = dayRepository.getFridayList();
        saturdayList = dayRepository.getSaturdayList();
        sundayList = dayRepository.getSundayList();
    }

    public void insert(SubjectMinimal subjectMinimal) {
        dayRepository.insert(subjectMinimal);
    }

    public void update(SubjectMinimal subjectMinimal) {
        dayRepository.update(subjectMinimal);
    }

    public void delete(SubjectMinimal subjectMinimal) {
        dayRepository.delete(subjectMinimal);
    }

    public void resetStatus(String day) {
        dayRepository.resetStatus(day);
    }

    public void deleteAllSubjects() {
        dayRepository.deleteAllSubjects();
    }

    public LiveData<List<SubjectMinimal>> getMondayList() {
        return mondayList;
    }

    public LiveData<List<SubjectMinimal>> getTuesdayList() {
        return tuesdayList;
    }

    public LiveData<List<SubjectMinimal>> getWednesdayList() {
        return wednesdayList;
    }

    public LiveData<List<SubjectMinimal>> getThursdayList() {
        return thursdayList;
    }

    public LiveData<List<SubjectMinimal>> getFridayList() {
        return fridayList;
    }

    public LiveData<List<SubjectMinimal>> getSaturdayList() {
        return saturdayList;
    }

    public LiveData<List<SubjectMinimal>> getSundayList() {
        return sundayList;
    }

    public LiveData<List<SubjectMinimal>> getDaySubjectList(String dayName) {
        switch (dayName.toLowerCase()) {
            case "monday":
                return getMondayList();
            case "tuesday":
                return getTuesdayList();
            case "wednesday":
                return getWednesdayList();
            case "thursday":
                return getThursdayList();
            case "friday":
                return getFridayList();
            case "saturday":
                return getSaturdayList();
            case "sunday":
                return getSundayList();
        }
        return null;
    }

    public List<SubjectMinimal> getSubjectList(String dayName) {
        return dayRepository.getSubjectList(dayName);
    }

    public void closeDB() {
        dayRepository.closeDB();
    }
}
