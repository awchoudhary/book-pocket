package com.awchoudhary.bookpocket.ui.viewbookscreen;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by awaeschoudhary on 3/26/17.
 * Note item for a book
 */

public class BookNote implements Serializable {
    private String noteId;
    private String bookId; // book that the note is for
    private String title;
    private String date;
    private String body; // the contents of the note
    private String color;
    private int SeqNo;

    public BookNote(){}

    public BookNote(String noteId, String bookId, String title, String date, String body){
        this.noteId = noteId;
        this.bookId = bookId;
        this.title = title;
        this.date = date;
        this.body = body;
    }

    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getBookId(){
        return bookId;
    }
    public void setBookId(String bookId){
        this.bookId = bookId;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public int getSeqNo() {
        return SeqNo;
    }
    public void setSeqNo(int seqNo) {
        SeqNo = seqNo;
    }
}
