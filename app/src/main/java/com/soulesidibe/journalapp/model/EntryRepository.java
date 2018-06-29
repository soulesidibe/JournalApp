package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.db.EntryDAO;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;

/**
 * Created on 6/27/18 at 11:35 AM
 * Project name : JournalApp
 */

public class EntryRepository implements EntryRepositoryInt {

    private final EntryDAO entryDAO;

    public EntryRepository(EntryDAO entryDAO) {
        this.entryDAO = entryDAO;
    }

    @Override
    public Observable<List<Entry>> getEntries() {
        return entryDAO.get().toObservable();
    }

    @Override
    public Completable saveEntry(final Entry entry) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                entryDAO.save(entry);
            }
        });
    }
}
