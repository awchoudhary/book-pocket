package com.awchoudhary.bookpocket.ui.searchscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.editbookscreen.CreateBookActivity;
import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by awaeschoudhary on 4/20/17.
 */

public class SearchBooksActivity extends AppCompatActivity {
    //create an empty search results adaptor, which will later be updated with the search result items
    SearchResultsAdaptor adaptor;
    private String shelfId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        if(intent.hasExtra("shelfId")){
            shelfId = intent.getStringExtra("shelfId");
        }

        //set the list adaptor
        final ListView searchResultsList = (ListView) findViewById(R.id.searchResultsList);
        adaptor = new SearchResultsAdaptor(this);
        searchResultsList.setAdapter(adaptor);

        //attach list item click event
        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                //get selected book and pass it into the next view
                Book selectedBook = (Book)searchResultsList.getItemAtPosition(position);
                Intent intent = new Intent(SearchBooksActivity.this, CreateBookActivity.class);
                intent.putExtra("book", selectedBook);
                intent.putExtra("shelfId", shelfId);
                startActivity(intent);
            }
        });

        EditText searchInput = (EditText) findViewById(R.id.input_search);

        searchInput.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer = new Timer();
                    private final long DELAY = 500; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        String query = ((EditText) findViewById(R.id.input_search)).getText().toString();
                                        GoogleBooksSearchTask task = new GoogleBooksSearchTask(SearchBooksActivity.this, adaptor);
                                        task.execute(query);
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

    }
}
