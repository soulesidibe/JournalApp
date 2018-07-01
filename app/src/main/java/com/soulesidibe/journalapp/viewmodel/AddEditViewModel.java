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

    private EntryRepositoryInt mRepository;

    private BaseSchedulerProvider mBaseScheduler;

    private MutableLiveData<Resource<List<Entry>>> mEntriesLiveData = new MutableLiveData<>();

    private CompositeDisposable mDisposables = new CompositeDisposable();

    public AddEditViewModel(EntryRepositoryInt repository, BaseSchedulerProvider baseScheduler) {
        this.mRepository = repository;
        this.mBaseScheduler = baseScheduler;
    }

    public void addEntry(Entry entry) {
        mEntriesLiveData.setValue(Resource.onLoading());
        Disposable disposable = mRepository.saveEntry(entry)
                .observeOn(mBaseScheduler.ui())
                .subscribeOn(mBaseScheduler.io())
                .subscribe();
        mDisposables.add(disposable);
    }

}
