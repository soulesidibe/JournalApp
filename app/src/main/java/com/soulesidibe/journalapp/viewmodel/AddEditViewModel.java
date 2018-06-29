package com.soulesidibe.journalapp.viewmodel;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.data.Resource;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created on 6/27/18 at 12:42 PM
 * Project name : JournalApp
 */

public class AddEditViewModel extends ViewModel {

    private EntryRepositoryInt repository;

    private BaseSchedulerProvider baseScheduler;

    private MutableLiveData<Resource<List<Entry>>> entriesLiveData = new MutableLiveData<>();

    private CompositeDisposable disposables = new CompositeDisposable();

    public AddEditViewModel(EntryRepositoryInt repository, BaseSchedulerProvider baseScheduler) {
        this.repository = repository;
        this.baseScheduler = baseScheduler;
    }

    public void addEntry(Entry entry) {
        entriesLiveData.setValue(Resource.onLoading());
        Disposable disposable = repository.saveEntry(entry)
                .observeOn(baseScheduler.ui())
                .subscribeOn(baseScheduler.io())
                .subscribe();
        disposables.add(disposable);
    }

}
