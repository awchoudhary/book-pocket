package com.awchoudhary.bookpocket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.view.animation.FastOutLinearInInterpolator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by awaeschoudhary on 3/4/17.
 * Handles all datebase interactions TODO: maybe split this class into multiple classes.
 */

//TODO: Exception handling for all these methods!
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Books";

    //books table
    private static final String TABLE_MY_BOOKS = "MyBooks";
    private static final String KEY_ID = "Id";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_SUB_TITLE = "Subtitle";
    private static final String KEY_AUTHOR = "Author";
    private static final String KEY_NUM_PAGES = "NumPages";
    private static final String KEY_COVER_URL = "CoverUrl";
    private static final String KEY_DESCRIPTION = "Description";
    private static final String KEY_RATINGS = "Ratings";
    private static final String KEY_NOTES = "Notes";
    private static final String KEY_DATE_STARTED = "DateStarted";
    private static final String KEY_DATE_COMPLETED = "DateCompleted";

    //BookNote table
    private static final String TABLE_BOOK_NOTES = "BookNotes";
    private static final String KEY_NOTE_ID = "Id";
    private static final String KEY_BOOK_ID = "BookId";
    private static final String KEY_NOTE_TITLE = "Title";
    private static final String KEY_NOTE_DATE = "Date";
    private static final String KEY_NOTE_BODY = "Body";
    private static final String KEY_NOTE_DELETED = "Deleted";


    //handles DateTime operations
    DateTimeHelper dateTimeHelper = new DateTimeHelper();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //called when the app is installed
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MY_BOOKS_TABLE = "CREATE TABLE " + TABLE_MY_BOOKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_SUB_TITLE + " TEXT," + KEY_AUTHOR + " TEXT,"
                + KEY_NUM_PAGES + " INTEGER," + KEY_COVER_URL + " TEXT," +
                KEY_DESCRIPTION + " TEXT," + KEY_RATINGS + " INTEGER," + KEY_NOTES
                + " TEXT," + KEY_DATE_STARTED + " TEXT," +
                KEY_DATE_COMPLETED + " TEXT)";

        String CREATE_BOOK_NOTES_TABLE = "CREATE TABLE " + TABLE_BOOK_NOTES + "("
                + KEY_NOTE_ID + " INTEGER PRIMARY KEY," + KEY_BOOK_ID + " INTEGER,"
                + KEY_NOTE_TITLE + " TEXT," + KEY_NOTE_DATE + " TEXT,"
                + KEY_NOTE_BODY + " TEXT," + KEY_NOTE_DELETED + " INTEGER)";

        db.execSQL(CREATE_MY_BOOKS_TABLE);
        db.execSQL(CREATE_BOOK_NOTES_TABLE);
    }

    //called when app is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_NOTES);

        // Create tables again
        onCreate(db);
    }

    public void createBook(Book b){
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, b.getName());
        values.put(KEY_SUB_TITLE, b.getSubtitle());
        values.put(KEY_AUTHOR, b.getAuthor());
        values.put(KEY_NUM_PAGES, b.getNumPages());
        values.put(KEY_COVER_URL, b.getCoverUrl());
        values.put(KEY_DESCRIPTION, b.getDescription());
        values.put(KEY_RATINGS, b.getRatings());
        values.put(KEY_NOTES, b.getNotes());
        values.put(KEY_DATE_STARTED, dateTimeHelper.toString(b.getDateStarted()));
        values.put(KEY_DATE_COMPLETED, dateTimeHelper.toString(b.getDateCompleted()));

        // Insert Row
        //2nd argument is String that specifies nullable column name
        db.insert(TABLE_MY_BOOKS, null, values);

        db.close(); // Closing database connection
    }

    // get all books from my books table
    public ArrayList<Book> getAllMyBooks() {
        ArrayList<Book> myBooks = new ArrayList<Book>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MY_BOOKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                //populate new book object with row values
                Book book = new Book();
                book.setId(cursor.getString(0));
                book.setName(cursor.getString(1));
                book.setSubtitle(cursor.getString(2));
                book.setAuthor(cursor.getString(3));
                book.setNumPages(Integer.parseInt(cursor.getString(4)));
                book.setCoverUrl(cursor.getString(5));
                book.setDescription(cursor.getString(6));
                book.setRatings(Integer.parseInt(cursor.getString(7)));
                book.setNotes(cursor.getString(8));
                book.setDateStarted((!cursor.getString(9).equals("")) ? dateTimeHelper.toDateTime(cursor.getString(9)) : null);
                book.setDateCompleted((!cursor.getString(10).equals("")) ? dateTimeHelper.toDateTime(cursor.getString(10)) : null);

                // Adding book to list
                myBooks.add(book);
            } while (cursor.moveToNext());
        }

        //close the cursor
        cursor.close();

        // return book list
        return myBooks;
    }

    // update a book
    public int updateBook(Book b) {
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, b.getName());
        values.put(KEY_SUB_TITLE, b.getSubtitle());
        values.put(KEY_AUTHOR, b.getAuthor());
        values.put(KEY_NUM_PAGES, b.getNumPages());
        values.put(KEY_COVER_URL, b.getCoverUrl());
        values.put(KEY_DESCRIPTION, b.getDescription());
        values.put(KEY_RATINGS, b.getRatings());
        values.put(KEY_NOTES, b.getNotes());
        values.put(KEY_DATE_STARTED, dateTimeHelper.toString(b.getDateStarted()));
        values.put(KEY_DATE_COMPLETED, dateTimeHelper.toString(b.getDateCompleted()));

        // updating row
        return db.update(TABLE_MY_BOOKS, values, KEY_ID + " = ?",
                new String[] { b.getId() });
    }

    // Deletes single book
    public void deleteBook(Book b) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MY_BOOKS, KEY_ID + " = ?",
                new String[] { b.getId()});
        db.close();
    }

    //update or create new BookNotes
    public void saveBookNotes(ArrayList<BookNote> notes){
        for(BookNote note : notes){
            //if the note does not have an ID, it does not exist in the db
            if(note.getNoteId() == null){
                createBookNote(note);
            }
            else{
                updateBookNote(note);
            }
        }
    }

    // get all notes for a particular book
    public ArrayList<BookNote> getBookNotes(String bookId) {
        ArrayList<BookNote> notes = new ArrayList<BookNote>();

        // Select all notes that are not deleted with the bookId
        String selectQuery = "SELECT  * FROM " + TABLE_BOOK_NOTES + " WHERE " + KEY_BOOK_ID + "=" + bookId
                + " AND " + KEY_NOTE_DELETED + "= 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                //populate new note object with row values
                BookNote note = new BookNote();
                note.setNoteId(cursor.getString(0));
                note.setBookId(cursor.getString(1));
                note.setTitle(cursor.getString(2));
                note.setDate((!cursor.getString(3).equals("")) ? dateTimeHelper.toDateTime(cursor.getString(3)) : null);
                note.setBody(cursor.getString(4));
                note.setDeleted(cursor.getString(5).equals("1"));

                // Add note to list
                notes.add(note);
            } while (cursor.moveToNext());
        }

        //close the cursor
        cursor.close();

        // return book list
        return notes;
    }

    //create new note
    private void createBookNote(BookNote note){
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, note.getNoteId());
        values.put(KEY_BOOK_ID, note.getBookId());
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_DATE, dateTimeHelper.toString(note.getDate()));
        values.put(KEY_NOTE_BODY, note.getBody());
        values.put(KEY_NOTE_DELETED, note.isDeleted());

        // Insert Row
        //2nd argument is String that specifies nullable column name
        db.insert(TABLE_BOOK_NOTES, null, values);

        db.close();
    }

    //update a BookNote row
    private int updateBookNote(BookNote note){
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_ID, note.getBookId());
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_DATE, dateTimeHelper.toString(note.getDate()));
        values.put(KEY_NOTE_BODY, note.getBody());
        values.put(KEY_NOTE_DELETED, note.isDeleted());

        // updating row
        return db.update(TABLE_BOOK_NOTES, values, KEY_ID + " = ?",
                new String[] { note.getNoteId() });
    }

}
