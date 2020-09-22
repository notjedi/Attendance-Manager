package com.attendancemanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.attendancemanager.repositories.DayRepository;
import com.attendancemanager.model.Friday;
import com.attendancemanager.model.Monday;
import com.attendancemanager.model.Saturday;
import com.attendancemanager.model.Sunday;
import com.attendancemanager.model.Thursday;
import com.attendancemanager.model.Tuesday;
import com.attendancemanager.model.Wednesday;

import java.util.List;

public class DayViewModel extends AndroidViewModel {

    private DayRepository dayRepository;
    private LiveData<List<Monday>> mondayList;
    private LiveData<List<Tuesday>> tuesdayList;
    private LiveData<List<Wednesday>> wednesdayList;
    private LiveData<List<Thursday>> thursdayList;
    private LiveData<List<Friday>> fridayList;
    private LiveData<List<Saturday>> saturdayList;
    private LiveData<List<Sunday>> sundayList;


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

    public void insert(String subjectName, String day) {
        switch (day.toLowerCase()) {
            case "monday":
                dayRepository.insert(new Monday(subjectName));
                break;
            case "tuesday":
                dayRepository.insert(new Tuesday(subjectName));
                break;
            case "wednesday":
                dayRepository.insert(new Wednesday(subjectName));
                break;
            case "thursday":
                dayRepository.insert(new Thursday(subjectName));
                break;
            case "friday":
                dayRepository.insert(new Friday(subjectName));
                break;
            case "saturday":
                dayRepository.insert(new Saturday(subjectName));
                break;
            case "sunday":
                dayRepository.insert(new Sunday(subjectName));
                break;
        }
    }

    public LiveData<List<Monday>> getMondayList() {
        return mondayList;
    }

    public LiveData<List<Tuesday>> getTuesdayList() {
        return tuesdayList;
    }

    public LiveData<List<Wednesday>> getWednesdayList() {
        return wednesdayList;
    }

    public LiveData<List<Thursday>> getThursdayList() {
        return thursdayList;
    }

    public LiveData<List<Friday>> getFridayList() {
        return fridayList;
    }

    public LiveData<List<Saturday>> getSaturdayList() {
        return saturdayList;
    }

    public LiveData<List<Sunday>> getSundayList() {
        return sundayList;
    }

    public void closeDB() {
        dayRepository.closeDB();
    }
}
