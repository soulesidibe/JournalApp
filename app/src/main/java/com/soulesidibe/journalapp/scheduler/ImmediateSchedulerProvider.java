package com.soulesidibe.journalapp.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 7/1/18 at 7:31 PM
 * Project name : JournalApp
 */

public class ImmediateSchedulerProvider implements BaseSchedulerProvider {

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
