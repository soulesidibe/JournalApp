package com.soulesidibe.journalapp.viewmodel;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.model.RemoteEntryDAOInt;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.data.Resource;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.soulesidibe.journalapp.model.data.Resource.ResourceState.ERROR;
import static com.soulesidibe.journalapp.model.data.Resource.ResourceState.SUCCESS;

/**
 * Created on 6/27/18 at 12:42 PM
 * Project name : JournalApp
 */

public class EntriesViewModel extends ViewModel {

    private EntryRepositoryInt mRepository;

    private RemoteEntryDAOInt mRemoteEntryDAO;

    private BaseSchedulerProvider mBaseScheduler;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    public EntriesViewModel(EntryRepositoryInt repository, RemoteEntryDAOInt remoteEntryDAO,
            BaseSchedulerProvider baseScheduler) {
        this.mRepository = repository;
        this.mRemoteEntryDAO = remoteEntryDAO;
        this.mBaseScheduler = baseScheduler;

    }

    public LiveData<Resource<List<Entry>>> getEntriesLiveData() {
        return Transformations
                .map(mRepository.getEntries(), new Function<List<Entry>, Resource<List<Entry>>>() {
                    @Override
                    public Resource<List<Entry>> apply(List<Entry> entries) {
                        if (entries == null || entries.isEmpty()) {
                            return new Resource<>(ERROR, null, "No Data found");
                        }

                        return new Resource<>(SUCCESS, entries, "");
                    }
                });
    }

    public void sync() {
        Disposable subscribe = mRemoteEntryDAO.pull()
                .observeOn(mBaseScheduler.ui())
                .subscribeOn(mBaseScheduler.io())
                .subscribe(new Consumer<List<Entry>>() {
                    @Override
                    public void accept(List<Entry> entries) throws Exception {
                        mRepository.sync(entries);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        mDisposables.add(subscribe);
    }

    public void clear() {
        mDisposables.clear();
    }

}
