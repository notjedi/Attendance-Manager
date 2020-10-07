package com.attendancemanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "thursday_table")
public class Thursday {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;
    private int status;

    public Thursday(String subjectName) {
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
