package com.awchoudhary.bookpocket;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


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
        final ListView searchResultsList = (ListView) findViewById(R.id.searchResultsList);
        adaptor = new SearchResultsAdaptor(this);
        searchResultsList.setAdapter(adaptor);

        //attach list item click event
        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                //get selected book and pass it into the next view
                Book selectedBook = (Book)searchResultsList.getItemAtPosition(position);
                Intent intent = new Intent(SearchBooksActivity.this, ViewBookActivity.class);
                intent.putExtra("book", selectedBook);
                startActivity(intent);
            }
        });


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
        String query = "";
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }

        GoogleBooksSearchTask task = new GoogleBooksSearchTask(this, adaptor);
        task.execute(query);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_search){
            return onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }
}
