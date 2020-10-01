package com.attendancemanager.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {Subject.class, Monday.class, Tuesday.class, Wednesday.class,
        Thursday.class, Friday.class, Saturday.class, Sunday.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public static final String DATABASE_NAME = "com.attendancemanager.db";
    private static Database instance;
    private static RoomDatabase.Callback prePopulateCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(new PrePopulateDatabase(instance)).start();
        }
    };

    public static synchronized Database getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class, DATABASE_NAME)
                    .addCallback(prePopulateCallback)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public void closeDB() {
        instance.close();
        instance = null;
    }

    public abstract SubjectDao subjectDao();
    public abstract MondayDao mondayDao();
    public abstract TuesdayDao tuesdayDao();
    public abstract WednesdayDao wednesdayDao();
    public abstract ThursdayDao thursdayDao();
    public abstract FridayDao fridayDao();
    public abstract SaturdayDao saturdayDao();
    public abstract SundayDao sundayDao();

    private static class PrePopulateDatabase implements Runnable {

        private SubjectDao subjectDao;

        PrePopulateDatabase(Database dataBase) {
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