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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

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
        book.setName(title);
        book.setSubtitle(subtitle);
        book.setAuthor(author);
        book.setNumPages(Integer.parseInt(numPages));
        book.setDateStarted(dateFormatter.parseDateTime(dateStarted));
        book.setDateCompleted(dateFormatter.parseDateTime(dateCompleted));
        book.setDescription(description);
        book.setNotes(notes);

        //if the id is the string 'new', a new book is being created
        if(id.equals("new")){
            dbHandler.createBook(book);
        }
        else{
            dbHandler.updateBook(book);
        }
    }

}
