package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created on 6/27/18 at 11:32 AM
 * Project name : JournalApp
 */

public interface EntryRepositoryInt {

    LiveData<List<Entry>> getEntries();

    Completable saveEntry(Entry entry);

    void sync(List<Entry> entries);

}
