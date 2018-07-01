package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created on 6/27/18 at 11:32 AM
 * Project name : JournalApp
 */

public interface EntryRepositoryInt {

    Observable<List<Entry>> getEntries();

    Completable saveEntry(Entry entry);

    void sync(List<Entry> entries);

}
