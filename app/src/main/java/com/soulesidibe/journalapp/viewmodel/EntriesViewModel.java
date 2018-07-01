package com.soulesidibe.journalapp.viewmodel;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.model.RemoteEntryDAOInt;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.data.Resource;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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

    private EntryRepositoryInt repository;

    private RemoteEntryDAOInt remoteEntryDAO;

    private BaseSchedulerProvider baseScheduler;

    private MutableLiveData<Resource<List<Entry>>> entriesLiveData = new MutableLiveData<>();

    private CompositeDisposable disposables = new CompositeDisposable();

    public EntriesViewModel(EntryRepositoryInt repository, RemoteEntryDAOInt remoteEntryDAO,
            BaseSchedulerProvider baseScheduler) {
        this.repository = repository;
        this.remoteEntryDAO = remoteEntryDAO;
        this.baseScheduler = baseScheduler;
    }

    public LiveData<Resource<List<Entry>>> getEntriesLiveData() {
        return entriesLiveData;
    }

    public void sync() {
        remoteEntryDAO.pull()
                .observeOn(baseScheduler.ui())
                .subscribeOn(baseScheduler.io())
                .subscribe(new Consumer<List<Entry>>() {
                    @Override
                    public void accept(List<Entry> entries) throws Exception {
                        repository.sync(entries);
                        entriesLiveData.setValue(new Resource<>(SUCCESS, entries, ""));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void getEntries() {
        entriesLiveData.setValue(Resource.onLoading());
        Disposable disposable = repository.getEntries()
                .observeOn(baseScheduler.ui())
                .subscribeOn(baseScheduler.io())
                .subscribe(new Consumer<List<Entry>>() {
                    @Override
                    public void accept(List<Entry> entries) throws Exception {
                        if (entries == null || entries.isEmpty()) {
                            Resource<List<Entry>> value = new Resource<>(ERROR, null,
                                    "No Data found");
                            entriesLiveData.setValue(value);
                            return;
                        }

                        entriesLiveData.setValue(new Resource<>(SUCCESS, entries, ""));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Resource<List<Entry>> value = new Resource<>(ERROR, null,
                                throwable.getMessage());
                        entriesLiveData.setValue(value);
                    }
                });
        disposables.add(disposable);
    }

    void clear() {
        disposables.clear();
    }

}
