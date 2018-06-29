package com.soulesidibe.journalapp.model.db;

import com.soulesidibe.journalapp.model.data.Entry;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created on 6/27/18 at 11:37 AM
 * Project name : JournalApp
 */

@Dao
public interface EntryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Entry entry);

    @Query("SELECT * FROM entry")
    Flowable<List<Entry>> get();
}
