package com.attendancemanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity(tableName = AppDatabase.SUBJECT_TABLE_NAME)
public class Subject {

    @Expose
    @PrimaryKey(autoGenerate = true)
    private int id;
    @Expose
    private String subjectName;
    @Expose
    private int totalClasses;
    @Expose
    private int attendedClasses;
    @Ignore
    private int status;

    public Subject(String subjectName, int attendedClasses, int totalClasses) {
        this.subjectName = subjectName;
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses;
    }

    @Ignore
    public Subject(String subjectName) {
        this(subjectName, 0, 0);
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
        this.totalClasses = this.totalClasses == -1 ? 0 : this.totalClasses;
    }

    public void incrementAttendedClasses() {
        this.attendedClasses++;
    }

    public void decrementAttendedClasses() {
        this.attendedClasses--;
        this.attendedClasses = this.attendedClasses == -1 ? 0 : this.attendedClasses;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getSubjectName() +
                " " + this.getAttendedClasses() +
                " " + this.getTotalClasses();
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
        } else
            return false;
    }
}
