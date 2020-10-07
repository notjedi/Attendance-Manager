package com.attendancemanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saturday_table")
public class Saturday {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;
    private int status;

    public Saturday(String subjectName) {
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
