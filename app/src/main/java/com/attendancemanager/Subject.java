package com.attendancemanager;

public class Subject {

    private String subjectName;
    private int totalClasses;
    private int attendedClasses;

    public Subject(String subjectName, int totalClasses, int attendedClasses) {
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
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
