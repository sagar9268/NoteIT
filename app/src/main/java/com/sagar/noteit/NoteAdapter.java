package com.sagar.noteit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Note> noteList;
    public NoteAdapter(Context mContext, List<Note> noteList){
        this.mContext = mContext;
        this.noteList = noteList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mNoteTitle;
        public TextView mNoteDate;
        public TextView mNoteText;

        public MyViewHolder(View view){
            super(view);
            mNoteTitle = view.findViewById(R.id.itemTitle);
            mNoteDate = view.findViewById(R.id.itemDate);
            mNoteText = view.findViewById(R.id.itemNote);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        int noteCount = 0;
        noteCount = getItemCount();
        if(noteCount>0){
            itemView = inflater.inflate(R.layout.item_note, parent, false);
        }

        else{
            itemView = inflater.inflate(R.layout.item_empty_note, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position){
        Note note = noteList.get(position);
        ((MyViewHolder)holder).mNoteTitle.setText(note.getTitle());
        ((MyViewHolder)holder).mNoteDate.setText(note.getNoteDate());
        ((MyViewHolder)holder).mNoteText.setText(note.getNoteText());
    }

    @Override
    public int getItemCount(){
        return noteList.size();
    }
}
