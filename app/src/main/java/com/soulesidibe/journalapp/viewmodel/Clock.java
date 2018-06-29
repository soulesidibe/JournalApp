package com.soulesidibe.journalapp.viewmodel;

/**
 * Created on 6/28/18 at 12:02 PM
 * Project name : JournalApp
 */

public class Clock implements ClockInt {

    @Override
    public long currentTime() {
        return System.currentTimeMillis();
    }
}
