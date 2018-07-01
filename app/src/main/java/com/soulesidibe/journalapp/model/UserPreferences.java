package com.soulesidibe.journalapp.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created on 6/27/18 at 11:49 AM
 * Project name : JournalApp
 */

public class UserPreferences implements UserPreferencesInt {

    private static final String IS_USER_LOGGED_IN = "is_user_logged_in";

    private static final String USER_ID = "user_id";

    private SharedPreferences pref;

    public UserPreferences(Context context) {
        pref = context.getSharedPreferences("diary_pref", Context.MODE_PRIVATE);
    }

    @Override
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_USER_LOGGED_IN, false);
    }

    @Override
    public void setLoggedIn(boolean value) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(IS_USER_LOGGED_IN, value);
        edit.apply();
    }

    @Override
    public String getUserId() {
        return pref.getString(USER_ID, null);
    }

    @Override
    public void setUserId(String id) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(USER_ID, id);
        edit.apply();
    }
}
