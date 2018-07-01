package com.soulesidibe.journalapp.model.db;

import com.soulesidibe.journalapp.model.data.Entry;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created on 6/27/18 at 11:37 AM
 * Project name : JournalApp
 */

@Dao
public interface EntryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Entry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkSave(List<Entry> entries);

    @Query("SELECT * FROM entry order by mDate desc")
    LiveData<List<Entry>> get();
}
