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
    private static final String TAG = "DBHelper";
    private static final String TABLE_SUBJECT_DETAILS_NAME = "subjectDetails";
    private static final String COLUMN_SUBJECT_NAME = "subjectName";
    private static final String COLUMN_ATTENDED_CLASSES = "attendedClasses";
    private static final String COLUMN_TOTAL_CLASSES = "totalClasses";

    public static final String TABLE_MONDAY_NAME = "monday";
    public static final String TABLE_TUESDAY_NAME = "tuesday";
    public static final String TABLE_WEDNESDAY_NAME = "wednesday";
    public static final String TABLE_THURSDAY_NAME = "thursday";
    public static final String TABLE_FRIDAY_NAME = "friday";
    public static final String TABLE_SATURDAY_NAME = "saturday";
    public static final String TABLE_SUNDAY_NAME = "sunday";

    public static final String[] TABLE_DAY_NAMES = {TABLE_MONDAY_NAME, TABLE_TUESDAY_NAME, TABLE_WEDNESDAY_NAME, TABLE_THURSDAY_NAME, TABLE_FRIDAY_NAME, TABLE_SATURDAY_NAME, TABLE_SUNDAY_NAME};


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /* Create tables when the app is started for the first time */

        String createSubjectDetailsTable = "CREATE TABLE " + TABLE_SUBJECT_DETAILS_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SUBJECT_NAME + " TEXT, "
                + COLUMN_ATTENDED_CLASSES + " INT, " + COLUMN_TOTAL_CLASSES + " INT)";

        sqLiteDatabase.execSQL(createSubjectDetailsTable);
        for (String day : TABLE_DAY_NAMES) {
            /* Create table for each day */
            String createDayTable = "CREATE TABLE " + day + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_SUBJECT_NAME + " TEXT)";
            sqLiteDatabase.execSQL(createDayTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /* Required */
    }

    public void addSubject(Subject subject) {
        /* Adds a subject to the TABLE_SUBJECT_DETAILS_NAME table */

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SUBJECT_NAME, subject.getSubjectName());
        contentValues.put(COLUMN_ATTENDED_CLASSES, subject.getAttendedClasses());
        contentValues.put(COLUMN_TOTAL_CLASSES, subject.getTotalClasses());

        database.insert(TABLE_SUBJECT_DETAILS_NAME, null, contentValues);
    }

    public void insertSubjectToDayTable(String day, String[] subjectNames) {
        /* Inserts a list of subject names to the specified day table */

        SQLiteDatabase database = getWritableDatabase();

        for (String subjectName : subjectNames) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_SUBJECT_NAME, subjectName);
            database.insert(day, null, contentValues);
        }
    }

    public void updateSubjectsTable(List<Subject> subjects) {
        /* Updates the TABLE_SUBJECT_DETAILS_NAME table with the List<Subject> provided */

        SQLiteDatabase database = getWritableDatabase();

        for (Subject subject : subjects) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_SUBJECT_NAME, subject.getSubjectName());
            contentValues.put(COLUMN_ATTENDED_CLASSES, subject.getAttendedClasses());
            contentValues.put(COLUMN_TOTAL_CLASSES, subject.getTotalClasses());
            database.update(TABLE_SUBJECT_DETAILS_NAME, contentValues,
                    COLUMN_SUBJECT_NAME + " = ?", new String[]{subject.getSubjectName()});
        }
    }

    public List<Subject> getSubjectsOfDay(String day) {
        /* Gets the subjects of the specified day */

        List<Subject> subjectList = new ArrayList<>();
        String queryString = "SELECT * FROM " + day;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(queryString, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            subjectList.add(getSubject(name));
        }

        cursor.close();
        return subjectList;
    }

    private Subject getSubject(String subjectName) {
        /* Gets the details of the subjectName from the TABLE_SUBJECT_DETAILS_NAME table */

        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_SUBJECT_DETAILS_NAME + " WHERE "
                + COLUMN_SUBJECT_NAME + " = ?";
        Subject subject;

        Cursor cursor = database.rawQuery(queryString, new String[]{subjectName});
        if (cursor.moveToNext()) {
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
        /* Gets the details of all subjects from the TABLE_SUBJECT_DETAILS_NAME table */

        SQLiteDatabase database = getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_SUBJECT_DETAILS_NAME;
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

    public void deleteSubjectFormDay(String subjectName, String[] days) {

        SQLiteDatabase database = getWritableDatabase();
        for (String day: days) {
            database.delete(day, COLUMN_SUBJECT_NAME + " = ?", new String[]{subjectName});
        }
    }

    public void deleteSubject(String subjectName) {
        /* Deletes the data of subjectName from the TABLE_SUBJECT_DETAILS_NAME table */

        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_SUBJECT_DETAILS_NAME, COLUMN_SUBJECT_NAME + " = ?"
                , new String[]{subjectName});
        for (String day: TABLE_DAY_NAMES) {
            database.delete(day, COLUMN_SUBJECT_NAME + " = ?", new String[]{subjectName});
        }
    }

    public void deleteAllSubject() {
        /* Deletes all data from the TABLE_SUBJECT_DETAILS_NAME table */

        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_SUBJECT_DETAILS_NAME, null, null);
        for (String day: TABLE_DAY_NAMES) {
            database.delete(day, null, null);
        }
    }
}