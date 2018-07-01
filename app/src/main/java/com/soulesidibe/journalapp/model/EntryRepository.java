package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.db.EntryDAO;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;

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

    private final EntryDAO localEntryDAO;

    private final RemoteEntryDAOInt remoteEntryDAO;

    private BaseSchedulerProvider schedulerProvider;

    public EntryRepository(EntryDAO entryDAO,
            RemoteEntryDAOInt remoteEntryDAO, BaseSchedulerProvider schedulerProvider) {
        this.localEntryDAO = entryDAO;
        this.remoteEntryDAO = remoteEntryDAO;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public Observable<List<Entry>> getEntries() {
        return localEntryDAO.get().toObservable();
    }

    @Override
    public Completable saveEntry(final Entry entry) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                localEntryDAO.save(entry);
                remoteEntryDAO.push(entry);
            }
        });
    }

    @Override
    public void sync(final List<Entry> entries) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                localEntryDAO.bulkSave(entries);
            }
        }).observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .subscribe();
    }
}
