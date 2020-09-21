package com.attendancemanager.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Subject.class}, version = 1)
public abstract class SubjectDataBase extends RoomDatabase {

    private static SubjectDataBase instance;
    public static final String DATABASE_NAME = "com.attendancemanager.db";

    public abstract SubjectDao subjectDao();

    public static synchronized SubjectDataBase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SubjectDataBase.class, DATABASE_NAME)
                    .addCallback(prePopulateCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback prePopulateCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(new PrePopulateDatabase(instance)).start();
        }
    };

    private static class PrePopulateDatabase implements Runnable {

        private SubjectDao subjectDao;

        PrePopulateDatabase(SubjectDataBase dataBase) {
            subjectDao = dataBase.subjectDao();
        }

        @Override
        public void run() {
            subjectDao.insert(new Subject("Math", 0, 0));
            subjectDao.insert(new Subject("English", 10, 50));
            subjectDao.insert(new Subject("Physics", 34, 49));
        }
    }
}