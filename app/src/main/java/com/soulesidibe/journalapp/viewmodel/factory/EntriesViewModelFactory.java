package com.soulesidibe.journalapp.viewmodel.factory;

import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.model.RemoteEntryDAOInt;
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

    private final EntryRepositoryInt mRepository;

    private final BaseSchedulerProvider mSchedulerProvider;

    private RemoteEntryDAOInt mRemoteEntryDAO;

    public EntriesViewModelFactory(EntryRepositoryInt repository, RemoteEntryDAOInt remoteEntryDAO,
            BaseSchedulerProvider schedulerProvider) {
        this.mRepository = repository;
        this.mRemoteEntryDAO = remoteEntryDAO;
        this.mSchedulerProvider = schedulerProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EntriesViewModel(mRepository, mRemoteEntryDAO, mSchedulerProvider);
    }
}
