package com.sagar.noteit;

public class Note {

    private String title;
    private String noteDate;
    private String noteText;

    public Note(){

    }

    public Note(String title, String noteDate, String noteText){

        this.title = title;
        this.noteDate = noteDate;
        this.noteText = noteText;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteDate(){
        return noteDate;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteText() {
        return noteText;
    }

}
