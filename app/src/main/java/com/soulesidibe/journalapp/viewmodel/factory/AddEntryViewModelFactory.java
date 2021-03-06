package com.soulesidibe.journalapp.viewmodel.factory;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;
import com.soulesidibe.journalapp.viewmodel.AddEditViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created on 6/27/18 at 1:25 PM
 * Project name : JournalApp
 */

public class AddEntryViewModelFactory implements ViewModelProvider.Factory {

    private final EntryRepositoryInt mRepository;

    private final BaseSchedulerProvider mSchedulerProvider;

    public AddEntryViewModelFactory(EntryRepositoryInt repository,
            BaseSchedulerProvider schedulerProvider) {
        this.mRepository = repository;
        this.mSchedulerProvider = schedulerProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddEditViewModel(mRepository, mSchedulerProvider);
    }
}
