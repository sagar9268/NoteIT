package com.sagar.noteit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryActivity extends AppCompatActivity {

    private Button mButtonCancel;
    private Button mButtonSave;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private ActionBar mActionBar;
    private CoordinatorLayout mCoordinatorLayout;
    private String ID_KEY = "id";
    private String mUserID;
    private String currentDate;
    private EditText mTitleEdit;
    private EditText mNoteEdit;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButtonCancel = findViewById(R.id.buttonCancel);
        mButtonSave = findViewById(R.id.buttonSave);
        mRadioGroup = findViewById(R.id.radioGroup);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout2);
        mTitleEdit = findViewById(R.id.editTextTitle);
        mNoteEdit = findViewById(R.id.editTextNote);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Add Note");
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);

        final Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString(ID_KEY);
        currentDate = new SimpleDateFormat("E, MMM d, yyyy", Locale.getDefault()).format(new Date());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mUserID).child("notes");

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EntryActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note mNote = new Note(mTitleEdit.getText().toString(),currentDate, mNoteEdit.getText().toString());

                int selectedID = mRadioGroup.getCheckedRadioButtonId();
                mRadioButton = findViewById(selectedID);
                mNoteDatabaseReference.child(mRadioButton.getText().toString()).push().setValue(mNote);

                Snackbar.make(mCoordinatorLayout, "Note added",Snackbar.LENGTH_SHORT).show();
                Intent i = new Intent(EntryActivity.this,MainActivity.class);
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
    }

}
