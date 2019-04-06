package com.famousnews.models;

import android.app.Application;

import com.famousnews.database.AppDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.initAppDatabase(getApplicationContext());
    }
}
