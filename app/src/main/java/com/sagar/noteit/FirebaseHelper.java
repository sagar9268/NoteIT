package com.sagar.noteit;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseHelper {
    DatabaseReference db;
    ArrayList<Note> notes = new ArrayList<>();
    ArrayList<Note> searchNote = new ArrayList<>();

    public FirebaseHelper(DatabaseReference db){
        this.db = db;
    }

    //implement fetch data and fill arraylist
    private void fetchData(DataSnapshot dataSnapshot){
        //notes.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Note note = ds.getValue(Note.class);
            Log.d(TAG,"Title: "+ note.getTitle());
            Log.d(TAG,"Note: "+ note.getNoteText());
            Log.d(TAG,"Date: "+note.getNoteDate());
            Log.d(TAG,"Image URL:"+ note.getNoteImageURL());
            notes.add(note);
        }
    }

    //implement search and fill array list
    private void searchData(DataSnapshot dataSnapshot, String key){
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Note note = ds.getValue(Note.class);
            Log.d(TAG,"Title: "+ note.getTitle());
            Log.d(TAG,"Note: "+ note.getNoteText());
            //search in title and note
            if(note.getTitle().toLowerCase().toString().contains(key) || note.getNoteText().toLowerCase().toString().contains(key)){
                searchNote.add(note);
            }
        }
    }

    //read by hooking onto database operation callbacks
    public ArrayList<Note> retrieve(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Failed to read data from the database.");

            }
        });
        Log.d(TAG,"Number of Notes: "+ notes.size());
        return notes;
    }

    //search notes by keyword
    public ArrayList<Note> search(final String key){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                searchData(dataSnapshot, key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                searchData(dataSnapshot, key);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Failed to read data from the database.");
            }
        });
        Log.d(TAG,"Number of matches found: "+searchNote.size());
        return searchNote;
    }
}
