package com.sagar.noteit;

public class Note {

    private String title;
    private String noteDate;
    private String noteText;
    private String noteImageURL;

    public Note(){

    }

    public Note(String title, String noteDate, String noteText, String noteImageURL){

        this.title = title;
        this.noteDate = noteDate;
        this.noteText = noteText;
        this.noteImageURL = noteImageURL;
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

    public void setNoteImageURL(String noteImageURL){
        this.noteImageURL = noteImageURL;
    }

    public String getNoteImageURL(){
        return noteImageURL;
    }

}
