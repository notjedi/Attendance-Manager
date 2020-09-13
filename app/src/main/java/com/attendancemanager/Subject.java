package com.attendancemanager;

import androidx.annotation.Nullable;

public class Subject {

    private String subjectName;
    private int totalClasses;
    private int attendedClasses;

    public Subject(String subjectName, int attendedClasses, int totalClasses) {
        this.subjectName = subjectName;
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses;
    }

    public Subject(String subjectName) {
        this(subjectName, 0, 0);
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 7 * (hash + this.subjectName.hashCode());
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Subject) {
            Subject subject = (Subject) obj;
            return (this.subjectName.equals(subject.getSubjectName()));
        }
        else
            return false;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public void incrementTotalClasses() {
        this.totalClasses++;
    }

    public void decrementTotalClasses() {
        this.totalClasses--;
    }

    public void incrementAttendedClasses() {
        this.attendedClasses++;
    }

    public void decrementAttendedClasses() {
        this.attendedClasses--;
    }
}
