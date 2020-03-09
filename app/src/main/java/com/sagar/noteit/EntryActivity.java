package com.sagar.noteit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryActivity extends AppCompatActivity {

    private Button mButtonCancel;
    private Button mButtonSave;
    private Button mButtonAddImage;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private ActionBar mActionBar;
    private CoordinatorLayout mCoordinatorLayout;
    private String ID_KEY = "id";
    private String mUserID;
    private String currentDate;
    private TextView mImageUploadInfo;
    private EditText mTitleEdit;
    private EditText mNoteEdit;
    private String mNoteDownloadURL = null;

    public static final int RC_PHOTO_PICKER = 1;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mNotePhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButtonCancel = findViewById(R.id.buttonCancel);
        mButtonSave = findViewById(R.id.buttonSave);
        mButtonAddImage = findViewById(R.id.buttonAddImage);
        mRadioGroup = findViewById(R.id.radioGroup);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout2);
        mTitleEdit = findViewById(R.id.editTextTitle);
        mNoteEdit = findViewById(R.id.editTextNote);
        mImageUploadInfo = findViewById(R.id.imageUploadInfo);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Add Note");
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mImageUploadInfo.setVisibility(View.INVISIBLE);
        mButtonSave.setEnabled(false);

        final Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString(ID_KEY);
        currentDate = new SimpleDateFormat("E, MMM d, yyyy", Locale.getDefault()).format(new Date());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mUserID);//.child("notes");
        mNotePhotosStorageReference = mFirebaseStorage.getReference().child("users").child(mUserID).child("note_images");


        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mButtonSave.setEnabled(true);
                } else {
                    mButtonSave.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    mButtonSave.setEnabled(true);
                } else {
                    mButtonSave.setEnabled(false);
                }

            }
        });

        mNoteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i,"Complete action using"), RC_PHOTO_PICKER);

            }
        });

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
                Note mNote;
                if(mNoteDownloadURL != null){
                    mNote = new Note(mTitleEdit.getText().toString(),currentDate, mNoteEdit.getText().toString(), mNoteDownloadURL);
                }
                else{
                    mNote = new Note(mTitleEdit.getText().toString(),currentDate, mNoteEdit.getText().toString(),null);
                }

                int selectedID = mRadioGroup.getCheckedRadioButtonId();
                mRadioButton = findViewById(selectedID);
                mNoteDatabaseReference.child(mRadioButton.getText().toString()+"-Notes").child("text").push().setValue(mNote);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mNotePhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            mImageUploadInfo.setVisibility(View.VISIBLE);

            //Upload files to Firebase Storage
            photoRef.putFile(selectedImageUri);
            mNotePhotosStorageReference.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        mImageUploadInfo.setText("Uploading failed!");
                        throw task.getException();
                    }
                    return mNotePhotosStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        mNoteDownloadURL = downloadUrl.toString();

                        //Note mNote = new Note(mTitleEdit.getText().toString(),currentDate, mNoteEdit.getText().toString(), downloadUrl.toString());
                        //mNoteDatabaseReference.push().setValue(mNote);
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mButtonAddImage.setText("Image Added");
                    mImageUploadInfo.setText("Uploading finished!");
                    mButtonSave.setEnabled(true);
                }
            });
        }
    }

}
