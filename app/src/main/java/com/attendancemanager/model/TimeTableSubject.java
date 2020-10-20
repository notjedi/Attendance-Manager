package com.attendancemanager.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity(tableName = AppDatabase.TIME_TABLE_NAME)
public class TimeTableSubject {

    public static final int NONE = -1;
    public static final int CANCELLED = 0;
    public static final int BUNKED = 1;
    public static final int ATTENDED = 2;

    @PrimaryKey(autoGenerate = true)
    @Expose
    private int id;
    @Expose
    private String subjectName;
    @Expose
    private int status;
    @Expose
    private String day;
    @Expose
    private boolean temp;

    public TimeTableSubject(String subjectName, int status, String day) {
        this.subjectName = subjectName;
        this.status = status;
        this.day = day.toLowerCase();
        this.temp = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @NonNull
    @Override
    public String toString() {
        return subjectName + " " +
                day + " " +
                status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day.toLowerCase();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }
}
