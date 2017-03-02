package com.awchoudhary.bookpocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

/**
 * Created by awaeschoudhary on 2/28/17.
 */

public class ViewBookActivity  extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewBook);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
