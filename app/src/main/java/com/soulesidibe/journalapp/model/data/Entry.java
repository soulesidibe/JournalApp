package com.soulesidibe.journalapp.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 6/27/18 at 11:09 AM
 * Project name : JournalApp
 */

@Entity
public class Entry {

    public static final String ENTRY_TITLE = "mTitle";

    public static final String ENTRY_CONTENT = "mContent";

    public static final String ENTRY_DATE = "mDate";

    private String mTitle;

    private String mContent;

    private String mKey;

    @PrimaryKey
    private long mDate;

    public Entry(String title, String content, long date) {
        this.mTitle = title;
        this.mContent = content;
        this.mDate = date;
    }

    public Entry() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ENTRY_TITLE, mTitle);
        map.put(ENTRY_CONTENT, mContent);
        map.put(ENTRY_DATE, mDate);
        return map;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "title='" + mTitle + '\'' +
                ", content='" + mContent + '\'' +
                ", date=" + mDate +
                '}';
    }
}
