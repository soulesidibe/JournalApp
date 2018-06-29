package com.soulesidibe.journalapp.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 6/27/18 at 11:14 AM
 * Project name : JournalApp
 */

public class AndroidScheduler implements BaseSchedulerProvider {


    private static volatile AndroidScheduler mInstance;

    private AndroidScheduler() {
    }

    public static AndroidScheduler getInstance() {
        if (mInstance == null) {
            synchronized (AndroidScheduler.class) {
                if (mInstance == null) {
                    mInstance = new AndroidScheduler();
                }
            }
        }
        return mInstance;
    }

    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
