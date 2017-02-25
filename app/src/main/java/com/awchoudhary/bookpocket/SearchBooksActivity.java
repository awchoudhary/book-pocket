package com.awchoudhary.bookpocket;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 2/23/17.
 */

public class SearchBooksActivity extends AppCompatActivity {

    //create an empty search results adaptor, which will later be updated with the search result items
    SearchResultsAdaptor adaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set the list adaptor
        ListView searchResultsList = (ListView) findViewById(R.id.searchResultsList);
        adaptor = new SearchResultsAdaptor(this);
        searchResultsList.setAdapter(adaptor);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }

        //create same book list for testing purposes
        ArrayList<Book> books = new ArrayList<Book>();
        Book b1 = new Book("Half a King", "Joe Abercrombie", "http://books.google.com/books/content?id=K1-LL9vlxZcC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api");
        Book b2 = new Book("Magicians", "Leive Grossman", "http://books.google.com/books/content?id=kJJdRiEVHxYC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api");
        books.add(b1);
        books.add(b2);

        adaptor.updateEntries(books);

        //LoadFeedData loadFeedData = new LoadFeedData(adapter);
        //loadFeedData.execute();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_search){
            return onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }
}
