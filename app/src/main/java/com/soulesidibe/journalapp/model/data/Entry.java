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

    public static final String ENTRY_TITLE = "title";

    public static final String ENTRY_CONTENT = "content";

    public static final String ENTRY_DATE = "date";

    private String title;

    private String content;

    @PrimaryKey
    private long date;

    public Entry(String title, String content, long date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Entry() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ENTRY_TITLE, title);
        map.put(ENTRY_CONTENT, content);
        map.put(ENTRY_DATE, date);
        return map;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
