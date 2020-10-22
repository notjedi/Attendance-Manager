package com.attendancemanager.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Subject.class, TimeTableSubject.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "com.attendancemanager.db";
    public static final String SUBJECT_TABLE_NAME = "subject_table";
    public static final String TIME_TABLE_NAME = "time_table";
    private static AppDatabase instance;
    private static final RoomDatabase.Callback prePopulateCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(new PrePopulateDatabase(instance)).start();
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .addCallback(prePopulateCallback)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract SubjectDao subjectDao();

    public abstract TimeTableDao timeTableDao();

    private static class PrePopulateDatabase implements Runnable {

        private final SubjectDao subjectDao;

        PrePopulateDatabase(AppDatabase dataBase) {
            subjectDao = dataBase.subjectDao();
        }

        @Override
        public void run() {
            subjectDao.insert(new Subject("Math", 20, 24));
            subjectDao.insert(new Subject("English", 10, 50));
        }
    }
}