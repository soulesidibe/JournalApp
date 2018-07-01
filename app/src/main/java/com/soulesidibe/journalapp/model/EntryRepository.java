package com.soulesidibe.journalapp.model;

import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.db.EntryDAO;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

/**
 * Created on 6/27/18 at 11:35 AM
 * Project name : JournalApp
 */

public class EntryRepository implements EntryRepositoryInt {

    private final EntryDAO mLocalEntryDAO;

    private final RemoteEntryDAOInt mRemoteEntryDAO;

    private BaseSchedulerProvider mSchedulerProvider;

    public EntryRepository(EntryDAO entryDAO,
            RemoteEntryDAOInt mRemoteEntryDAO, BaseSchedulerProvider mSchedulerProvider) {
        this.mLocalEntryDAO = entryDAO;
        this.mRemoteEntryDAO = mRemoteEntryDAO;
        this.mSchedulerProvider = mSchedulerProvider;
    }

    @Override
    public LiveData<List<Entry>> getEntries() {
        return Transformations.map(mLocalEntryDAO.get(), new Function<List<Entry>, List<Entry>>() {
            @Override
            public List<Entry> apply(List<Entry> input) {
                return input;
            }
        });
    }

    @Override
    public Completable saveEntry(final Entry entry) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                String key = mRemoteEntryDAO.push(entry);
                entry.setKey(key);
                mLocalEntryDAO.save(entry);
            }
        });
    }

    @Override
    public void sync(final List<Entry> entries) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                mLocalEntryDAO.bulkSave(entries);
            }
        }).observeOn(mSchedulerProvider.ui())
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }
}
