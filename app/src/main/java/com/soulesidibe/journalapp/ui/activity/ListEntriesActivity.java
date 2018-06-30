package com.soulesidibe.journalapp.ui.activity;

import com.soulesidibe.journalapp.R;
import com.soulesidibe.journalapp.internal.Injector;
import com.soulesidibe.journalapp.model.UserPreferencesInt;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.model.data.Resource;
import com.soulesidibe.journalapp.ui.adapter.EntryAdapter;
import com.soulesidibe.journalapp.viewmodel.EntriesViewModel;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListEntriesActivity extends AppCompatActivity implements EntryAdapter.OnItemClick {

    private RecyclerView recyclerView;

    private FloatingActionButton add;

    private TextView noEntryTv;

    private TextView notLoggedInTv;

    private Button connectBtn;

    private ProgressBar loading;

    private UserPreferencesInt userPreferences;

    private EntriesViewModel viewModel;

    private EntryAdapter adapter;

    private List<Entry> entries = new ArrayList<>();

    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPreferences = Injector.providesPreferences(this);
        viewModel = Injector.providesEntriesViewModel(this);

        recyclerView = findViewById(R.id.id_list_entries_rv_list);
        add = findViewById(R.id.id_list_entries_fab_add);
        noEntryTv = findViewById(R.id.id_list_entries_tv_no_entry);
        notLoggedInTv = findViewById(R.id.id_list_entries_tv_not_loggedin);
        connectBtn = findViewById(R.id.id_list_entries_btn_connect);
        loading = findViewById(R.id.id_list_entries_pb_loader);

        viewModel.getEntriesLiveData().observe(this, new Observer<Resource<List<Entry>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Entry>> listResource) {
                if (!isLoggedIn) {
                    showConnect();
                    return;
                }
                if (listResource == null) {
                    showEmpty();
                    return;
                }
                Resource.ResourceState state = listResource.getState();
                if (state == Resource.ResourceState.SUCCESS) {
                    handleData(listResource.getData());
                } else if (state == Resource.ResourceState.LOADING) {
                    showLoading();
                } else if (state == Resource.ResourceState.ERROR) {
                    showEmpty();
                }
            }
        });

        if (!userPreferences.isLoggedIn()) {
            isLoggedIn = false;
            showConnect();
        } else {
            isLoggedIn = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getEntries();
    }

    private void handleData(List<Entry> data) {
        if (entries.isEmpty()) {
            entries.addAll(data);
            initRecyclerView();
        } else {
            updateRecyclerView(data);
        }
        showEntries();
    }

    private void updateRecyclerView(List<Entry> data) {
        adapter.update(data);
    }

    @Override
    public void onclick(Entry entry) {
        Intent intent = new Intent(this, AddEditEntryActivity.class);
        intent.putExtra("action", "show");
        intent.putExtra("entry_title", entry.getTitle());
        intent.putExtra("entry_content", entry.getContent());
        intent.putExtra("entry_date", entry.getDate());
        startActivity(intent);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new EntryAdapter(entries, this);
        recyclerView.setAdapter(adapter);
    }

    private void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        noEntryTv.setVisibility(View.INVISIBLE);
        notLoggedInTv.setVisibility(View.INVISIBLE);
        connectBtn.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    private void showEntries() {
        recyclerView.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        noEntryTv.setVisibility(View.INVISIBLE);
        notLoggedInTv.setVisibility(View.INVISIBLE);
        connectBtn.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.INVISIBLE);
        add.setVisibility(View.VISIBLE);
        noEntryTv.setVisibility(View.VISIBLE);
        notLoggedInTv.setVisibility(View.INVISIBLE);
        connectBtn.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    private void showConnect() {
        recyclerView.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        noEntryTv.setVisibility(View.INVISIBLE);
        notLoggedInTv.setVisibility(View.VISIBLE);
        connectBtn.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.id_list_entries_btn_connect) {
            connectGoogle();
        } else if (id == R.id.id_list_entries_fab_add) {
            Intent intent = new Intent(this, AddEditEntryActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        }
    }

    private void connectGoogle() {

    }
}
