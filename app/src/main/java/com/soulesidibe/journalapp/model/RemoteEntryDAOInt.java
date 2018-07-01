package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;

import java.util.List;

import io.reactivex.Single;

/**
 * Created on 6/30/18 at 11:16 AM
 * Project name : JournalApp
 */

public interface RemoteEntryDAOInt {

    String push(Entry entry);

    void bulkPush(List<Entry> entries);

    Single<List<Entry>> pull();
}
