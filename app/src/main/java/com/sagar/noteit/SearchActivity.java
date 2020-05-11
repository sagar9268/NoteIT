package com.sagar.noteit;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchActivity extends AppCompatActivity {

    private Button mButtonCancel;
    private Button mButtonSearch;
    private EditText mSearchText;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> objectList1;
    private ArrayList<Note> objectList2;
    FirebaseHelper helper1,helper2;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButtonCancel = findViewById(R.id.buttonSearchCancel);
        mButtonSearch = findViewById(R.id.buttonSearch);
        mSearchText = findViewById(R.id.editTextSearch);
        recyclerView = findViewById(R.id.searchNoteListRec);
        objectList1 = new ArrayList<>();
        objectList2 = new ArrayList<>();
        objectList1.clear();
        objectList2.clear();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getUid());


        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper1 = new FirebaseHelper(mNoteDatabaseReference.child("Personal-Notes"));
                objectList1 = helper1.search(mSearchText.getText().toString().toLowerCase());
                Log.d(TAG,"Object list 1 size: "+ objectList1.size());
                helper2 = new FirebaseHelper(mNoteDatabaseReference.child("Professional-Notes"));
                objectList2 = helper2.search(mSearchText.getText().toString().toLowerCase());
                Log.d(TAG,"Object list 2 size: "+ objectList2.size());
                objectList1.addAll(objectList2);//corrections needed
                Log.d(TAG,"Object list 1 + 2 size: "+ objectList1.size());
                noteAdapter = new NoteAdapter(SearchActivity.this, objectList1);
                recyclerView.setAdapter(noteAdapter);
                noteAdapter.notifyDataSetChanged();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            noteAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
