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
import java.util.Date;
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
    private static final String KEY_DATE_STARTED = "DateStarted";
    private static final String KEY_DATE_COMPLETED = "DateCompleted";
    private static final String KEY_DATE_TO_READ_BY = "DateToReadBy";
    private static final String KEY_DATE_PUBLISHED = "DatePublished";
    private static final String KEY_DATE_ADDED = "DateAdded";
    private static final String KEY_READING_STATUS = "ReadingStatus";
    private static final String KEY_ROW_COLOR = "RowColor";
    private static final String KEY_PUBLISHER = "Publisher";
    private static final String KEY_ISBN = "ISBN";
    private static final String KEY_FAVORITE = "Favorite";
    private static final String KEY_BOOK_SEQ_NO = "SequenceNumber";
    private static final String KEY_CURRENT_PAGE = "CurrentPage";

    //BookNote table
    private static final String TABLE_BOOK_NOTES = "BookNotes";
    private static final String KEY_NOTE_ID = "Id";
    private static final String KEY_BOOK_ID = "BookId";
    private static final String KEY_NOTE_TITLE = "Title";
    private static final String KEY_NOTE_DATE = "Date";
    private static final String KEY_NOTE_BODY = "Body";
    private static final String KEY_COLOR = "Color";
    private static final String KEY_NOTE_SEQ_NO = "SequenceNumber";

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
                KEY_DESCRIPTION + " TEXT," +  KEY_DATE_STARTED + " TEXT,"
                + KEY_DATE_COMPLETED + " TEXT," + KEY_DATE_TO_READ_BY + " TEXT," +
                KEY_DATE_PUBLISHED + " TEXT," + KEY_DATE_ADDED + " TEXT," +
                KEY_READING_STATUS + " TEXT," + KEY_ROW_COLOR + " TEXT," +
                KEY_PUBLISHER + " TEXT," + KEY_ISBN + " TEXT," + KEY_FAVORITE +
                " INTEGER," + KEY_BOOK_SEQ_NO + " INTEGER," + KEY_CURRENT_PAGE + " INTEGER)";

        String CREATE_BOOK_NOTES_TABLE = "CREATE TABLE " + TABLE_BOOK_NOTES + "("
                + KEY_NOTE_ID + " INTEGER PRIMARY KEY," + KEY_BOOK_ID + " INTEGER,"
                + KEY_NOTE_TITLE + " TEXT," + KEY_NOTE_DATE + " TEXT,"
                + KEY_NOTE_BODY + " TEXT," + KEY_COLOR + " TEXT," + KEY_NOTE_SEQ_NO + " INTEGER)";

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
        values.put(KEY_DATE_STARTED, DateTimeHelper.toString(b.getDateStarted()));
        values.put(KEY_DATE_COMPLETED, DateTimeHelper.toString(b.getDateCompleted()));
        values.put(KEY_DATE_TO_READ_BY, DateTimeHelper.toString(b.getDateToReadBy()));
        values.put(KEY_DATE_PUBLISHED, DateTimeHelper.toString(b.getDatePublished()));
        values.put(KEY_DATE_ADDED, DateTimeHelper.toString(b.getDateAdded()));
        values.put(KEY_READING_STATUS, b.getReadingStatus());
        values.put(KEY_ROW_COLOR, b.getRowColor());
        values.put(KEY_PUBLISHER, b.getPublisher());
        values.put(KEY_ISBN, b.getIsbn());
        values.put(KEY_FAVORITE, b.isFavorite());
        values.put(KEY_BOOK_SEQ_NO, b.getSeqNo());
        values.put(KEY_CURRENT_PAGE, b.getCurrentPage());

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
                book.setDateStarted(DateTimeHelper.toDateTime(cursor.getString(7)));
                book.setDateCompleted(DateTimeHelper.toDateTime(cursor.getString(8)));
                book.setDateToReadBy(DateTimeHelper.toDateTime(cursor.getString(9)));
                book.setDatePublished(DateTimeHelper.toDateTime(cursor.getString(10)));
                book.setDateAdded(DateTimeHelper.toDateTime(cursor.getString(11)));
                book.setReadingStatus(cursor.getString(12));
                book.setRowColor(cursor.getString(13));
                book.setPublisher(cursor.getString(14));
                book.setIsbn(cursor.getString(15));
                book.setFavorite((Integer.parseInt(cursor.getString(16)) == 0) ? false : true);
                book.setSeqNo(Integer.parseInt(cursor.getString(17)));
                book.setCurrentPage(Integer.parseInt(cursor.getString(18)));

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
        values.put(KEY_DATE_STARTED, DateTimeHelper.toString(b.getDateStarted()));
        values.put(KEY_DATE_COMPLETED, DateTimeHelper.toString(b.getDateCompleted()));
        values.put(KEY_DATE_TO_READ_BY, DateTimeHelper.toString(b.getDateToReadBy()));
        values.put(KEY_DATE_PUBLISHED, DateTimeHelper.toString(b.getDatePublished()));
        values.put(KEY_DATE_ADDED, DateTimeHelper.toString(b.getDateAdded()));
        values.put(KEY_READING_STATUS, b.getReadingStatus());
        values.put(KEY_ROW_COLOR, b.getRowColor());
        values.put(KEY_PUBLISHER, b.getPublisher());
        values.put(KEY_ISBN, b.getIsbn());
        values.put(KEY_FAVORITE, b.isFavorite());
        values.put(KEY_BOOK_SEQ_NO, b.getSeqNo());
        values.put(KEY_CURRENT_PAGE, b.getCurrentPage());

        // updating row
        return db.update(TABLE_MY_BOOKS, values, KEY_ID + " = ?",
                new String[] { b.getId() });
    }

    // Deletes single book and all of its associated notes
    public void deleteBook(Book b) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete book
        db.delete(TABLE_MY_BOOKS, KEY_ID + " = ?",
                new String[] { b.getId()});

        //delete all note for book
        db.delete(TABLE_BOOK_NOTES, KEY_BOOK_ID + " = ?",
                new String[] { b.getId()});

        db.close();
    }

    //TODO: This method is deprecated - remove it.
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
        String selectQuery = "SELECT  * FROM " + TABLE_BOOK_NOTES + " WHERE " + KEY_BOOK_ID + "=" + bookId;

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
                note.setDate((DateTimeHelper.toDateTime(cursor.getString(3))));
                note.setBody(cursor.getString(4));
                note.setColor(cursor.getString(5));
                note.setSeqNo(Integer.parseInt(cursor.getString(6)));

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
    public void createBookNote(BookNote note){
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, note.getNoteId());
        values.put(KEY_BOOK_ID, note.getBookId());
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_DATE, DateTimeHelper.toString(note.getDate()));
        values.put(KEY_NOTE_BODY, note.getBody());
        values.put(KEY_COLOR, note.getColor());
        values.put(KEY_NOTE_SEQ_NO, note.getSeqNo());

        // Insert Row
        //2nd argument is String that specifies nullable column name
        db.insert(TABLE_BOOK_NOTES, null, values);

        db.close();
    }

    //update a BookNote row
    public int updateBookNote(BookNote note){
        SQLiteDatabase db = this.getWritableDatabase();

        //place all values
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_ID, note.getBookId());
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_DATE, DateTimeHelper.toString(note.getDate()));
        values.put(KEY_NOTE_BODY, note.getBody());
        values.put(KEY_COLOR, note.getColor());
        values.put(KEY_NOTE_SEQ_NO, note.getSeqNo());

        // update row
        return db.update(TABLE_BOOK_NOTES, values, KEY_ID + " = ?",
                new String[] { note.getNoteId() });
    }

    // Deletes single book note
    public void deleteBookNote(BookNote note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOK_NOTES, KEY_NOTE_ID + " = ?",
                new String[] { note.getNoteId()});
        db.close();
    }

}
