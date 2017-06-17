package com.awchoudhary.bookpocket.ui.viewbookscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.editbookscreen.CreateBookActivity;
import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;
import com.awchoudhary.bookpocket.ui.mybooksscreen.MainActivity;
import com.awchoudhary.bookpocket.util.DatabaseHandler;
import com.awchoudhary.bookpocket.util.ReadingStatus;

/**
 * Created by awaeschoudhary on 3/21/17.
 * Manages all tabs for view and create book.
 */

public class TabManagerActivity extends AppCompatActivity{
    Book book = new Book(); //book object that is being edited or created
    ViewPager viewPager;
    PagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab_manager);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tab_manager);
        setSupportActionBar(toolbar);

        //get book to be viewed within the activity
        if(intent.hasExtra("book")){
            book = (Book)intent.getSerializableExtra("book");
        }

        //set titles for toolbar
        getSupportActionBar().setTitle(book.getName());

        //create tablayout and add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes & Progress"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), book);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab_manager_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_edit) {
            //call create/edit book activity
            Intent intent = new Intent(this, CreateBookActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        }
        else if(id == R.id.action_delete) {
            //delete and navigate to mybooks page.
            DatabaseHandler db = new DatabaseHandler(this);
            db.deleteBook(book);
            Toast.makeText(getApplicationContext(), "Deleted " + book.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TabManagerActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
