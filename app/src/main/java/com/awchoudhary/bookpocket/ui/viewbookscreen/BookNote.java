package com.awchoudhary.bookpocket.ui.viewbookscreen;

import org.joda.time.DateTime;

/**
 * Created by awaeschoudhary on 3/26/17.
 * Note item for a book
 */

public class BookNote {
    private String noteId;
    private String bookId; // book that the note is for
    private String title;
    private DateTime date;
    private String body; // the contents of the note
    private String color;
    private int SeqNo;

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

    public DateTime getDate() {
        return date;
    }
    public void setDate(DateTime date) {
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
