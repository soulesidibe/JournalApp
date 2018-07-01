package com.soulesidibe.journalapp.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.soulesidibe.journalapp.model.data.Entry;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created on 6/30/18 at 11:19 AM
 * Project name : JournalApp
 */

public class RemoteEntryDAO implements RemoteEntryDAOInt {

    private final FirebaseDatabase mDatabase;

    private final UserPreferencesInt mPreferences;

    public RemoteEntryDAO(FirebaseDatabase mDatabase,
            UserPreferencesInt mPreferences) {
        this.mDatabase = mDatabase;
        this.mPreferences = mPreferences;
    }


    @Override
    public String push(Entry entry) {
        DatabaseReference reference = mDatabase.getReference(mPreferences.getUserId());
        String key;
        if (TextUtils.isEmpty(entry.getKey())) {
            key = reference.child(mPreferences.getUserId()).push().getKey();
        } else {
            key = entry.getKey();
        }

        Map<String, Object> entryMap = entry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, entryMap);
        reference.updateChildren(childUpdates);
        return key;
    }

    @Override
    public void bulkPush(List<Entry> entries) {
        DatabaseReference reference = mDatabase.getReference(mPreferences.getUserId());
        Map<String, Object> childUpdates = new HashMap<>();
        for (Entry entry : entries) {
            String key = reference.child(mPreferences.getUserId()).push().getKey();
            Map<String, Object> entryMap = entry.toMap();
            childUpdates.put(key, entryMap);
        }

        reference.updateChildren(childUpdates);
    }

    @Override
    public Single<List<Entry>> pull() {
        return Single.create(new SingleOnSubscribe<List<Entry>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Entry>> emitter) throws Exception {
                DatabaseReference reference = mDatabase.getReference(mPreferences.getUserId());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Entry> entries = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Entry entry = snapshot.getValue(Entry.class);
                            if (entry != null && entry.getContent() != null
                                    && entry.getTitle() != null && entry.getDate() != 0) {
                                entry.setKey(snapshot.getKey());
                                entries.add(entry);
                            }
                        }
                        if (!emitter.isDisposed()) {
                            emitter.onSuccess(entries);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (!emitter.isDisposed()) {
                            emitter.onError(databaseError.toException());
                        }
                    }
                });

            }
        });
    }
}
