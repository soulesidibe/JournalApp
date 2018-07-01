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
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListEntriesActivity extends AppCompatActivity implements EntryAdapter.OnItemClick {

    private static final int RC_SIGN_IN = 12000;

    private RecyclerView mRecyclerView;

    private FloatingActionButton mAdd;

    private TextView mTextViewNoEntryTv;

    private TextView mTextViewNotLoggedInTv;

    private SignInButton mBtnConnect;

    private ProgressBar mProgressbarLoading;

    private UserPreferencesInt mUserPreferences;

    private EntriesViewModel mViewModel;

    private EntryAdapter mAdapter;

    private List<Entry> mEntries = new ArrayList<>();

    private boolean mIsLoggedIn = false;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseUser mCurrentUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserPreferences = Injector.providesPreferences(this);
        mViewModel = Injector.providesEntriesViewModel(this);

        mRecyclerView = findViewById(R.id.id_list_entries_rv_list);
        mAdd = findViewById(R.id.id_list_entries_fab_add);
        mTextViewNoEntryTv = findViewById(R.id.id_list_entries_tv_no_entry);
        mTextViewNotLoggedInTv = findViewById(R.id.id_list_entries_tv_not_loggedin);
        mBtnConnect = findViewById(R.id.id_list_entries_btn_connect);
        mProgressbarLoading = findViewById(R.id.id_list_entries_pb_loader);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectGoogle();
            }
        });

        if (!mUserPreferences.isLoggedIn()) {
            mIsLoggedIn = false;
            showConnect();
        } else {
            mIsLoggedIn = true;
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
        if (!mUserPreferences.isLoggedIn()) {
            mCurrentUser = mAuth.getCurrentUser();
            if (mCurrentUser != null) {
                mUserPreferences.setLoggedIn(true);
                mUserPreferences.setUserId(mCurrentUser.getUid());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.clear();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mCurrentUser = mAuth.getCurrentUser();
                            mUserPreferences.setLoggedIn(true);
                            mUserPreferences.setUserId(mCurrentUser.getUid());
                            mIsLoggedIn = true;
                            mViewModel.sync();
                            showEmpty();
                        } else {
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
        if (!mUserPreferences.isLoggedIn() && mCurrentUser == null) {
            showConnect();
            return;
        }
        getData();
    }

    private void getData() {
        mViewModel.getEntriesLiveData().observe(this, new Observer<Resource<List<Entry>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Entry>> listResource) {
                if (!mIsLoggedIn) {
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
            } catch (ApiException ignored) {
            }
        }
    }

    private void handleData(List<Entry> data) {
        if (mEntries.isEmpty()) {
            mEntries.addAll(data);
            initRecyclerView();
        } else {
            mEntries.clear();
            mEntries.addAll(data);
            updateRecyclerView(data);
        }
        showEntries();
    }

    private void updateRecyclerView(List<Entry> data) {
        mAdapter.update(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onclick(Entry entry) {
        Intent intent = new Intent(this, AddEditEntryActivity.class);
        intent.putExtra("action", "show");
        intent.putExtra("entry_title", entry.getTitle());
        intent.putExtra("entry_content", entry.getContent());
        intent.putExtra("entry_date", entry.getDate());
        if (!TextUtils.isEmpty(entry.getKey())) {
            intent.putExtra("entry_key", entry.getKey());
        }
        startActivity(intent);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new EntryAdapter(mEntries, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mAdd.setVisibility(View.INVISIBLE);
        mTextViewNoEntryTv.setVisibility(View.INVISIBLE);
        mTextViewNotLoggedInTv.setVisibility(View.INVISIBLE);
        mBtnConnect.setVisibility(View.INVISIBLE);
        mProgressbarLoading.setVisibility(View.VISIBLE);
    }

    private void showEntries() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdd.setVisibility(View.VISIBLE);
        mTextViewNoEntryTv.setVisibility(View.INVISIBLE);
        mTextViewNotLoggedInTv.setVisibility(View.INVISIBLE);
        mBtnConnect.setVisibility(View.INVISIBLE);
        mProgressbarLoading.setVisibility(View.INVISIBLE);
    }

    private void showEmpty() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mAdd.setVisibility(View.VISIBLE);
        mTextViewNoEntryTv.setVisibility(View.VISIBLE);
        mTextViewNotLoggedInTv.setVisibility(View.INVISIBLE);
        mBtnConnect.setVisibility(View.INVISIBLE);
        mProgressbarLoading.setVisibility(View.INVISIBLE);
    }

    private void showConnect() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mAdd.setVisibility(View.INVISIBLE);
        mTextViewNoEntryTv.setVisibility(View.INVISIBLE);
        mTextViewNotLoggedInTv.setVisibility(View.VISIBLE);
        mBtnConnect.setVisibility(View.VISIBLE);
        mProgressbarLoading.setVisibility(View.INVISIBLE);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.id_list_entries_fab_add) {
            Intent intent = new Intent(this, AddEditEntryActivity.class);
            intent.putExtra("action", "mAdd");
            startActivity(intent);
        }
    }

    private void connectGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
