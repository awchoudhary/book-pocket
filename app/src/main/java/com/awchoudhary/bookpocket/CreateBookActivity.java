package com.awchoudhary.bookpocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by awaeschoudhary on 3/5/17.
 */

public class CreateBookActivity extends AppCompatActivity {
    //True if book is being created and False if it is being edited. True by default.
    boolean newBook = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        //if a book was passed to the view, populate inputs.
        if(intent.hasExtra("book")){
            Book book = (Book)intent.getSerializableExtra("book");

            //if book has an ID, it already exists in the DB and is therefore being edited.
            if(book.getId() != null){
                newBook = false;
            }

            populate(book);
        }

        //set the date edit texts as date pickers
        new DatePickerCustom(this, R.id.dateStartedInput);
        new DatePickerCustom(this, R.id.dateCompletedInput);

        //attached even handlers
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                Intent intent = new Intent(CreateBookActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    //populates inputs in view with provided parameter
    private void populate(Book book){
        //used for parsing dates to string
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

        ((EditText) findViewById(R.id.bookId)).setText(book.getId());
        ((EditText) findViewById(R.id.titleInput)).setText(book.getName());
        ((EditText) findViewById(R.id.subtitleInput)).setText(book.getSubtitle());
        ((EditText) findViewById(R.id.authorInput)).setText(book.getAuthor());
        ((EditText) findViewById(R.id.numPagesInput)).setText(Integer.toString(book.getNumPages()));
        ((EditText) findViewById(R.id.descriptionInput)).setText(book.getDescription());
        ((EditText) findViewById(R.id.notesInput)).setText(book.getNotes());

        //set dates if non null
        ((EditText) findViewById(R.id.dateStartedInput)).setText((book.getDateStarted() != null) ? dateFormatter.print(book.getDateStarted()) : "");
        ((EditText) findViewById(R.id.dateCompletedInput)).setText((book.getDateCompleted() != null) ? dateFormatter.print(book.getDateCompleted()) : "");
    }

    private void save(){
        //handles db interactions
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        //date formatter used to parse date strings
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");


        //get all field inputs
        String id = ((EditText) findViewById(R.id.bookId)).getText().toString();
        String title = ((EditText) findViewById(R.id.titleInput)).getText().toString();
        String subtitle = ((EditText) findViewById(R.id.subtitleInput)).getText().toString();
        String author = ((EditText) findViewById(R.id.authorInput)).getText().toString();
        String numPages = ((EditText) findViewById(R.id.numPagesInput)).getText().toString();
        String dateStarted = ((EditText) findViewById(R.id.dateStartedInput)).getText().toString();
        String dateCompleted = ((EditText) findViewById(R.id.dateCompletedInput)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionInput)).getText().toString();
        String notes = ((EditText) findViewById(R.id.notesInput)).getText().toString();

        //TODO: Validation

        //populate new book object with inputs
        Book book = new Book();
        //It doesn't matter what the ID is when the book is being created, so don't worry about null id.
        //ID should be non-null when book is being edited. Tab bhi null hui tou mai kya karoon?
        book.setId(id);
        book.setName(title);
        book.setSubtitle(subtitle);
        book.setAuthor(author);
        book.setNumPages((!numPages.equals("")) ? Integer.parseInt(numPages) : 0);
        book.setDateStarted((!dateStarted.equals("")) ? dateFormatter.parseDateTime(dateStarted) : null);
        book.setDateCompleted((!dateCompleted.equals("")) ? dateFormatter.parseDateTime(dateCompleted) : null);
        book.setDescription(description);
        book.setNotes(notes);

        if(newBook){
            dbHandler.createBook(book);
        }
        else{
            dbHandler.updateBook(book);
        }
    }

}
