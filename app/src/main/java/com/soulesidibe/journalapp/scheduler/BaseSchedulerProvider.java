package com.soulesidibe.journalapp.scheduler;

import io.reactivex.Scheduler;

/**
 * Created on 6/27/18 at 11:11 AM
 * Project name : JournalApp
 */

public interface BaseSchedulerProvider {

    Scheduler computation();

    Scheduler io();

    Scheduler ui();
}
