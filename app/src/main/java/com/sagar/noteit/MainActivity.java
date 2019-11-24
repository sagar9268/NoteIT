package com.sagar.noteit;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final String ID = "123";
    public static final int RC_SIGN_IN = 1;

    private String mUsername;
    private String ID_KEY = "id";
    private String mUserID;
    private Button mButtonAddNote;
    private ActionBar mActionBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //private RecyclerView.Adapter mAdapter;

    private NoteAdapter adapter;
    private ArrayList<Note> objectList;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;
    private ChildEventListener mChildEventListener;

    FirebaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButtonAddNote = findViewById(R.id.buttonAddNote);
        recyclerView = findViewById(R.id.notelistrec);

        mUsername = ANONYMOUS;
        mUserID = ID;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);

        objectList = new ArrayList<>();

        objectList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Log.d(TAG,"User ID: "+user.getUid());
        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getUid());//.child("users").child(mUserID).child("notes");

        helper = new FirebaseHelper(mNoteDatabaseReference);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        objectList = helper.retrieve();
        adapter = new NoteAdapter(MainActivity.this,objectList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();




        mButtonAddNote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,EntryActivity.class);
                i.putExtra(ID_KEY,mUserID);
                startActivity(i);
                finish();
            }
        });

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //signed in
                    onSignedInInitialize(user.getDisplayName(),user.getUid());
                }
                else{
                    //signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setLogo(R.drawable.app_logo)
                            .build(),
                            RC_SIGN_IN

                    );
                }
            }
        };

        /*
        mNoteDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Note mNote = dataSnapshot1.getValue(Note.class);
                    objectList.add(mNote);
                }
                adapter = new NoteAdapter(MainActivity.this,objectList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_notes) {
            adapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.action_sign_out){
            //sign out
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        //detachDatabaseReadListener();
        //adapter.clear();
        objectList.clear();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        adapter.notifyDataSetChanged();
    }

    private void onSignedInInitialize(String username,String userID)
    {
        mUsername = username;
        mUserID = userID;

        adapter.notifyDataSetChanged();
        //attachDatabaseReadListener();
    }

    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;
        mUserID = ID;
        //adapter.clear();
        objectList.clear();
        adapter.notifyDataSetChanged();
        //detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Note mNote = dataSnapshot.getValue(Note.class);
                    objectList.add(mNote);

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mNoteDatabaseReference.addChildEventListener(mChildEventListener);
        }



    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mNoteDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
