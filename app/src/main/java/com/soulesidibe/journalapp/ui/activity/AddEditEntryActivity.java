package com.soulesidibe.journalapp.ui.activity;

import com.soulesidibe.journalapp.R;
import com.soulesidibe.journalapp.internal.Injector;
import com.soulesidibe.journalapp.model.data.Entry;
import com.soulesidibe.journalapp.viewmodel.AddEditViewModel;
import com.soulesidibe.journalapp.viewmodel.ClockInt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class AddEditEntryActivity extends AppCompatActivity {

    private final ClockInt clock = Injector.getClock();

    private EditText title;

    private EditText content;

    private AddEditViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);

        viewModel = Injector.providesAddEntryViewModel(this);

        title = findViewById(R.id.id_add_edit_edt_title);
        content = findViewById(R.id.id_add_edit_edt_content);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = this.title.getText().toString();
        String content = this.content.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            return;
        }

        Entry entry = new Entry(title, content, clock.currentTime());
        viewModel.addEntry(entry);
    }
}
