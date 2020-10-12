package com.attendancemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.attendancemanager.model.TimeTableSubject;
import com.attendancemanager.repositories.DayRepository;

import java.util.List;

public class DayViewModel extends AndroidViewModel {

    public static final int NONE = -1;
    public static final int CANCELLED = 0;
    public static final int BUNKED = 1;
    public static final int ATTENDED = 2;

    private DayRepository dayRepository;
    private LiveData<List<TimeTableSubject>> mondayList;
    private LiveData<List<TimeTableSubject>> tuesdayList;
    private LiveData<List<TimeTableSubject>> wednesdayList;
    private LiveData<List<TimeTableSubject>> thursdayList;
    private LiveData<List<TimeTableSubject>> fridayList;
    private LiveData<List<TimeTableSubject>> saturdayList;
    private LiveData<List<TimeTableSubject>> sundayList;


    public DayViewModel(@NonNull Application application) {
        super(application);

        dayRepository = DayRepository.getInstance(application);
        mondayList = dayRepository.getSubjectsOfDayLiveData("monday");
        tuesdayList = dayRepository.getSubjectsOfDayLiveData("tuesday");
        wednesdayList = dayRepository.getSubjectsOfDayLiveData("wednesday");
        thursdayList = dayRepository.getSubjectsOfDayLiveData("thursday");
        fridayList = dayRepository.getSubjectsOfDayLiveData("friday");
        saturdayList = dayRepository.getSubjectsOfDayLiveData("saturday");
        sundayList = dayRepository.getSubjectsOfDayLiveData("sunday");
    }

    public void insert(TimeTableSubject timeTableSubject) {
        dayRepository.insert(timeTableSubject);
    }

    public void update(TimeTableSubject timeTableSubject) {
        dayRepository.update(timeTableSubject);
    }

    public void delete(TimeTableSubject timeTableSubject) {
        dayRepository.delete(timeTableSubject);
    }

    public void resetStatus(String day) {
        dayRepository.resetStatus(day);
    }

    public void deleteLimited(String day, int limit) {
        dayRepository.deleteLimited(day, limit);
    }

    public void deleteSubjectByName(String subjectName) {
        dayRepository.deleteSubjectByName(subjectName);
    }

    public void deleteTempSubjects() {
        dayRepository.deleteTempSubjects();
    }

    public void deleteAllSubjects() {
        dayRepository.deleteAllSubjects();
    }

    public LiveData<List<TimeTableSubject>> getSubjectsOfDayLiveData(String day) {
        switch (day.toLowerCase()) {
            case "monday":
                return mondayList;
            case "tuesday":
                return tuesdayList;
            case "wednesday":
                return wednesdayList;
            case "thursday":
                return thursdayList;
            case "friday":
                return fridayList;
            case "saturday":
                return saturdayList;
            case "sunday":
                return sundayList;
        }
        return null;
    }

    public List<TimeTableSubject> getSubjectsOfDay(String dayName) {
        return dayRepository.getSubjectsOfDay(dayName);
    }

    public void closeDB() {
        dayRepository.closeDB();
    }
}
