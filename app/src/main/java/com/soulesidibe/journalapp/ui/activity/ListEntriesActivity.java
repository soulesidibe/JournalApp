package com.soulesidibe.journalapp.ui.activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListEntriesActivity extends AppCompatActivity implements EntryAdapter.OnItemClick {

    private static final int RC_SIGN_IN = 12000;

    private RecyclerView recyclerView;

    private FloatingActionButton add;

    private TextView noEntryTv;

    private TextView notLoggedInTv;

    private SignInButton connectBtn;

    private ProgressBar loading;

    private UserPreferencesInt userPreferences;

    private EntriesViewModel viewModel;

    private EntryAdapter adapter;

    private List<Entry> entries = new ArrayList<>();

    private boolean isLoggedIn = false;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
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

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectGoogle();
            }
        });

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!userPreferences.isLoggedIn()) {
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                userPreferences.setLoggedIn(true);
                userPreferences.setUserId(currentUser.getUid());
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ListEntries", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ListEntries", "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            userPreferences.setLoggedIn(true);
                            userPreferences.setUserId(currentUser.getUid());
                            isLoggedIn = true;
                            viewModel.sync();
                            showEmpty();
                        } else {
                            Log.w("ListEntries", "signInWithCredential:failure",
                                    task.getException());
                            Toast.makeText(ListEntriesActivity.this,
                                    "Connexion with google failed. Try again", Toast.LENGTH_SHORT)
                                    .show();
                            showConnect();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userPreferences.isLoggedIn() || currentUser != null) {
            viewModel.getEntries();
        } else {
            showConnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ListEntries", "Google sign in failed", e);
                // ...
            }
        }
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
        if (id == R.id.id_list_entries_fab_add) {
            Intent intent = new Intent(this, AddEditEntryActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        }
    }

    private void connectGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
