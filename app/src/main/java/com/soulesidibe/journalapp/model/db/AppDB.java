package com.soulesidibe.journalapp.model.db;

import com.soulesidibe.journalapp.model.data.Entry;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created on 6/27/18 at 11:44 AM
 * Project name : JournalApp
 */

@Database(entities = {Entry.class}, version = 1)
public abstract class AppDB extends RoomDatabase {

    public abstract EntryDAO getEntryDAO();
}
