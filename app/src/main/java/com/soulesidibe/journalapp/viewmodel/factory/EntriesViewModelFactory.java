package com.soulesidibe.journalapp.viewmodel.factory;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;
import com.soulesidibe.journalapp.viewmodel.EntriesViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created on 6/27/18 at 1:25 PM
 * Project name : JournalApp
 */

public class EntriesViewModelFactory implements ViewModelProvider.Factory {

    private final EntryRepositoryInt repository;

    private final BaseSchedulerProvider schedulerProvider;

    public EntriesViewModelFactory(EntryRepositoryInt repository,
            BaseSchedulerProvider schedulerProvider) {
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EntriesViewModel(repository, schedulerProvider);
    }
}
