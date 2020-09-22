package com.attendancemanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wednesday_table")
public class Wednesday {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;

    public Wednesday(String subjectName) {
        this.subjectName = subjectName;
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
}
