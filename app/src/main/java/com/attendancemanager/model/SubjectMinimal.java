package com.attendancemanager.model;

import androidx.room.Ignore;


public class SubjectMinimal {

    protected int id;
    protected String subjectName;
    @Ignore
    protected String day;

    public SubjectMinimal(String subjectName) {
        this.subjectName = subjectName;
    }

    @Ignore
    public SubjectMinimal(String subjectName, String day) {
        this.subjectName = subjectName;
        this.day = day.toLowerCase();
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
