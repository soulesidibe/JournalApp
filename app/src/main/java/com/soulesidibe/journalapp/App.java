package com.soulesidibe.journalapp;

import com.soulesidibe.journalapp.internal.Injector;
import com.soulesidibe.journalapp.model.db.AppDB;

import android.app.Application;

/**
 * Created on 6/27/18 at 11:04 AM
 * Project name : JournalApp
 */

public class App extends Application {

    public static AppDB sAppDB;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppDB = Injector.providesDB(this);
    }
}
