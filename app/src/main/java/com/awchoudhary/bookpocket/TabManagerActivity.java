package com.awchoudhary.bookpocket;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by awaeschoudhary on 3/21/17.
 * Manages all tabs for view and create book.
 */

public class TabManagerActivity extends AppCompatActivity{
    int currentTabNumber; //keeps track of tab number being viewed
    Book book = new Book(); //book object that is being edited or created
    boolean isNewBook = true; //indicates if book is being created. If false, a previously existing book is being edited.
    boolean isEdit = false; //indicates if book is being edited (New or editing existing). If false, the book is being viewed.
    ViewPager viewPager;
    PagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //update isEdit
        if(intent.hasExtra("isEdit")){
            isEdit = intent.getExtras().getBoolean("isEdit");
        }

        //if a book was passed to the view, update book object and isNewBook.
        if(intent.hasExtra("book")){
            book = (Book)intent.getSerializableExtra("book");

            //if book has an ID, it already exists in the DB and is therefore being edited.
            if(book.getId() != null){
                isNewBook = false;
            }
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Notes & Progress"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), book, isEdit);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                currentTabNumber = tab.getPosition();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        //hide/show toolbar menu buttons based on isEdit. i.e. show add for isEdit=false and edit and delete for isEdit=true
        if(isEdit){
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            int currentItem = viewPager.getCurrentItem();
            EditNotesTabFragment tab = (EditNotesTabFragment) adapter.getFragmentByPosition(1);
            // always check for null
            if(tab != null){
                tab.saveNotes();
            }
            return true;
        }
        else if(id == R.id.action_edit) {
            //call the tab manager activity again with isedit as true.
            Intent intent = new Intent(this, TabManagerActivity.class);
            intent.putExtra("book", book);
            intent.putExtra("isEdit", true);
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

    private String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }
}
