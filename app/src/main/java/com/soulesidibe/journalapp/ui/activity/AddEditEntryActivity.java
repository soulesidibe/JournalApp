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

    private final ClockInt mClock = Injector.getClock();

    private EditText mEditTextTitle;

    private EditText mEditTextContent;

    private TextView mTextViewTitle;

    private TextView mTextViewContent;

    private AddEditViewModel mViewModel;

    private Entry mEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);
        mViewModel = Injector.providesAddEntryViewModel(this);

        mEditTextTitle = findViewById(R.id.id_add_edit_edt_title);
        mEditTextContent = findViewById(R.id.id_add_edit_edt_content);
        mTextViewTitle = findViewById(R.id.id_add_edit_tv_title);
        mTextViewContent = findViewById(R.id.id_add_edit_tv_content);

        mTextViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForm();
                if (mEntry != null) {
                    mEditTextTitle.setText(mEntry.getTitle());
                    mEditTextContent.setText(mEntry.getContent());
                }
            }
        });

        mTextViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForm();
                if (mEntry != null) {
                    mEditTextTitle.setText(mEntry.getTitle());
                    mEditTextContent.setText(mEntry.getContent());
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action.equals("add")) {
            showForm();
            setTitle(R.string.str_title_list_entries_add);
        } else if (action.equals("show")) {
            String title = intent.getStringExtra("entry_title");
            setTitle(title);
            showEntry();
            mEntry = new Entry(title,
                    intent.getStringExtra("entry_content"), intent.getLongExtra("entry_date", 0));
            if (intent.hasExtra("entry_key")) {
                mEntry.setKey(intent.getStringExtra("entry_key"));
            }
            mTextViewTitle.setText(mEntry.getTitle());
            mTextViewContent.setText(mEntry.getContent());
        }
    }

    private void showForm() {
        mEditTextTitle.setVisibility(View.VISIBLE);
        mEditTextContent.setVisibility(View.VISIBLE);
        mTextViewTitle.setVisibility(View.INVISIBLE);
        mTextViewContent.setVisibility(View.INVISIBLE);
    }

    private void showEntry() {
        mEditTextTitle.setVisibility(View.INVISIBLE);
        mEditTextContent.setVisibility(View.INVISIBLE);
        mTextViewTitle.setVisibility(View.VISIBLE);
        mTextViewContent.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = this.mEditTextTitle.getText().toString();
        String content = this.mEditTextContent.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            return;
        }

        if (this.mEntry == null) {
            Entry entry = new Entry(title, content, mClock.currentTime());
            mViewModel.addEntry(entry);
        } else {
            Entry entry = new Entry(title, content, this.mEntry.getDate());
            entry.setKey(this.mEntry.getKey());
            mViewModel.addEntry(entry);
        }
    }
}
