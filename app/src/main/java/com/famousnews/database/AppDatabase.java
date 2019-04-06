package com.famousnews.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.famousnews.models.Article;

@Database(entities = {Article.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDAO userDao();

    public static void initAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "newsdatabase").allowMainThreadQueries().build();
        }
    }

    public static AppDatabase getAppDatabase() {
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
