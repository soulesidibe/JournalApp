package com.soulesidibe.journalapp.internal;

import com.google.firebase.database.FirebaseDatabase;

import com.soulesidibe.journalapp.model.EntryRepository;
import com.soulesidibe.journalapp.model.EntryRepositoryInt;
import com.soulesidibe.journalapp.model.RemoteEntryDAO;
import com.soulesidibe.journalapp.model.RemoteEntryDAOInt;
import com.soulesidibe.journalapp.model.UserPreferences;
import com.soulesidibe.journalapp.model.UserPreferencesInt;
import com.soulesidibe.journalapp.model.db.AppDB;
import com.soulesidibe.journalapp.model.db.EntryDAO;
import com.soulesidibe.journalapp.scheduler.AndroidScheduler;
import com.soulesidibe.journalapp.scheduler.BaseSchedulerProvider;
import com.soulesidibe.journalapp.viewmodel.AddEditViewModel;
import com.soulesidibe.journalapp.viewmodel.Clock;
import com.soulesidibe.journalapp.viewmodel.ClockInt;
import com.soulesidibe.journalapp.viewmodel.EntriesViewModel;
import com.soulesidibe.journalapp.viewmodel.factory.AddEntryViewModelFactory;
import com.soulesidibe.journalapp.viewmodel.factory.EntriesViewModelFactory;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * Created on 6/27/18 at 11:06 AM
 * Project name : JournalApp
 */

public class Injector {

    public static AppDB providesDB(Context context) {
        return Room.databaseBuilder(context,
                AppDB.class, "journal-db").build();
    }

    public static UserPreferencesInt providesPreferences(Context context) {
        return new UserPreferences(context);
    }

    public static EntryRepositoryInt providesRepository(Context context) {
        return new EntryRepository(providesEntryDAO(context), providesRemoteEntry(context),
                providesScheduler());
    }

    public static RemoteEntryDAOInt providesRemoteEntry(Context context) {
        return new RemoteEntryDAO(FirebaseDatabase.getInstance(), providesPreferences(context));
    }

    public static EntryDAO providesEntryDAO(Context context) {
        return providesDB(context).getEntryDAO();
    }

    public static BaseSchedulerProvider providesScheduler() {
        return AndroidScheduler.getInstance();
    }

    public static EntriesViewModel providesEntriesViewModel(FragmentActivity activity) {
        return ViewModelProviders
                .of(activity, new EntriesViewModelFactory(providesRepository(activity),
                        providesRemoteEntry(activity), providesScheduler()))
                .get(EntriesViewModel.class);
    }

    public static ClockInt getClock() {
        return new Clock();
    }

    public static AddEditViewModel providesAddEntryViewModel(FragmentActivity activity) {
        return ViewModelProviders
                .of(activity, new AddEntryViewModelFactory(providesRepository(activity),
                        providesScheduler()))
                .get(AddEditViewModel.class);
    }
}
