package com.soulesidibe.journalapp.ui.activity;

import com.soulesidibe.journalapp.R;
import com.soulesidibe.journalapp.internal.Injector;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.viewmodel.AddEditViewModel;
import com.soulesidibe.journalapp.viewmodel.ClockInt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddEditEntryActivity extends AppCompatActivity {

    private final ClockInt clock = Injector.getClock();

    private EditText title;

    private EditText content;

    private TextView tvTitle;

    private TextView tvContent;

    private AddEditViewModel viewModel;

    private Entry entry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);
        viewModel = Injector.providesAddEntryViewModel(this);

        title = findViewById(R.id.id_add_edit_edt_title);
        content = findViewById(R.id.id_add_edit_edt_content);
        tvTitle = findViewById(R.id.id_add_edit_tv_title);
        tvContent = findViewById(R.id.id_add_edit_tv_content);

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForm();
                if (entry != null) {
                    title.setText(entry.getTitle());
                    content.setText(entry.getContent());
                }
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForm();
                if (entry != null) {
                    title.setText(entry.getTitle());
                    content.setText(entry.getContent());
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action.equals("add")) {
            showForm();
        } else if (action.equals("show")) {
            showEntry();
            entry = new Entry(intent.getStringExtra("entry_title"),
                    intent.getStringExtra("entry_content"), intent.getLongExtra("entry_date", 0));
            if (intent.hasExtra("entry_key")) {
                entry.setKey(intent.getStringExtra("entry_key"));
            }
            tvTitle.setText(entry.getTitle());
            tvContent.setText(entry.getContent());
        }
    }

    private void showForm() {
        title.setVisibility(View.VISIBLE);
        content.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.INVISIBLE);
        tvContent.setVisibility(View.INVISIBLE);
    }

    private void showEntry() {
        title.setVisibility(View.INVISIBLE);
        content.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = this.title.getText().toString();
        String content = this.content.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            return;
        }

        if (this.entry == null) {
            Entry entry = new Entry(title, content, clock.currentTime());
            viewModel.addEntry(entry);
        } else {
            Entry entry = new Entry(title, content, this.entry.getDate());
            entry.setKey(this.entry.getKey());
            viewModel.addEntry(entry);
        }
    }
}
