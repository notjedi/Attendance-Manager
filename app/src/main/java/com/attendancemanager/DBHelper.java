package com.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "com.attendancemanager.db";
    public static final String SUBJECT_DETAILS_TABLE_NAME = "subjectDetails";
    public static final String COLUMN_SUBJECT_NAME = "subjectName";
    public static final String COLUMN_ATTENDED_CLASSES = "attendedClasses";
    public static final String COLUMN_TOTAL_CLASSES = "totalClasses";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createSubjectDetailsTable = "CREATE TABLE " + SUBJECT_DETAILS_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SUBJECT_NAME + " TEXT, "
                + COLUMN_ATTENDED_CLASSES + " INT, " + COLUMN_TOTAL_CLASSES + " INT)";
        sqLiteDatabase.execSQL(createSubjectDetailsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addSubject(Subject subject) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SUBJECT_NAME, subject.getSubjectName());
        contentValues.put(COLUMN_ATTENDED_CLASSES, subject.getAttendedClasses());
        contentValues.put(COLUMN_TOTAL_CLASSES, subject.getTotalClasses());

        database.insert(SUBJECT_DETAILS_TABLE_NAME, null, contentValues);
    }

    public Subject getSubject(String subjectName) {
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + SUBJECT_DETAILS_TABLE_NAME + " WHERE " + COLUMN_SUBJECT_NAME + " = ?";
        Subject subject;

        Cursor cursor = database.rawQuery(queryString, new String[]{subjectName});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int attendedClasses = cursor.getInt(2);
            int totalClasses = cursor.getInt(3);

            subject = new Subject(name, attendedClasses, totalClasses);
            cursor.close();
            return subject;
        } else {
            cursor.close();
            return null;
        }
    }

    public List<Subject> getAllSubjects() {
        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + SUBJECT_DETAILS_TABLE_NAME;
        List<Subject> subjectList = new ArrayList<>();

        Cursor cursor = database.rawQuery(queryString, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int attendedClasses = cursor.getInt(2);
            int totalClasses = cursor.getInt(3);

            Subject subject = new Subject(name, attendedClasses, totalClasses);
            subjectList.add(subject);
        }

        cursor.close();
        return subjectList;
    }

    public boolean deleteSubject(String subjectName) {

        SQLiteDatabase database = getWritableDatabase();
        return database.delete(SUBJECT_DETAILS_TABLE_NAME, COLUMN_SUBJECT_NAME + " = ?", new String[]{subjectName}) > 0;
    }

    public boolean deleteAllSubject() {

        SQLiteDatabase database = getWritableDatabase();
        return database.delete(SUBJECT_DETAILS_TABLE_NAME, null, null) > 0;
    }
}










