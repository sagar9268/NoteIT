package com.sagar.noteit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Note> noteList;
    public NoteAdapter(Context mContext, ArrayList<Note> noteList){
        this.mContext = mContext;
        this.noteList = noteList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mNoteTitle;
        public TextView mNoteDate;
        public TextView mNoteText;
        public ImageView mNoteImage;

        public MyViewHolder(View view){
            super(view);
            mNoteTitle = view.findViewById(R.id.itemTitle);
            mNoteDate = view.findViewById(R.id.itemDate);
            mNoteText = view.findViewById(R.id.itemNote);
            mNoteImage = view.findViewById(R.id.itemImage);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView;
        //int noteCount = 0;
        //noteCount = getItemCount();
        //if(noteCount>0){
            itemView = inflater.inflate(R.layout.item_note, parent, false);
        //}
        //else{
        //    itemView = inflater.inflate(R.layout.item_empty_note, parent, false);
       // }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Note note = noteList.get(position);
        ((MyViewHolder)holder).mNoteTitle.setText(note.getTitle());
        ((MyViewHolder)holder).mNoteDate.setText(note.getNoteDate());
        ((MyViewHolder)holder).mNoteText.setText(note.getNoteText());
        boolean isImage = note.getNoteImageURL() != null;
        if(isImage){
            ((MyViewHolder)holder).mNoteImage.setVisibility(View.VISIBLE);
            Glide.with(((MyViewHolder)holder).mNoteImage.getContext()).load(note.getNoteImageURL()).into(((MyViewHolder)holder).mNoteImage);
        }
        else{
            ((MyViewHolder)holder).mNoteImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return noteList.size();
    }

}
