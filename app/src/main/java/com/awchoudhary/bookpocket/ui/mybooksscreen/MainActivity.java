package com.awchoudhary.bookpocket.ui.mybooksscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.searchscreen.SearchBooksActivity;
import com.awchoudhary.bookpocket.ui.signinscreen.SignInActivity;
import com.github.clans.fab.FloatingActionButton;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    //id for default shelves that all users will have
    private final String MY_BOOKS_SHELF_ID = "my-books";

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private RecyclerView booksList;
    private ShelfAdapter adapter;
    private DatabaseReference mDatabase;
    private NavigationView navigationView;
    private ArrayList<Shelf> shelves = new ArrayList<>(); //List of user's custom shelves
    private String currentShelfId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        //set current shelf id to mybooks
        currentShelfId = MY_BOOKS_SHELF_ID;

        //get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // RecyclerView to populate
        booksList = (RecyclerView) findViewById(R.id.recycle_view_books);
        booksList.setLayoutManager(new LinearLayoutManager(this));
        booksList.setNestedScrollingEnabled(true);

        // set adapter for listview
        adapter = new ShelfAdapter(this, new ArrayList<Book>(), new ArrayList<Book>());
        booksList.setAdapter(adapter);

        //attach event handlers
        FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fab_main_activity);
        fabMenu.setClosedOnTouchOutside(true);

        //set event handlers for fab buttons
        FloatingActionButton addBySearchButton = (FloatingActionButton) findViewById(R.id.button_add_by_search);
        addBySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchBooksActivity.class);
                intent.putExtra("shelfId", currentShelfId);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        //load shelves into nav drawer if they are not already loaded
        if(shelves.size() == 0){
            populateNavigationDrawer();
        }

        //load books for current shelf
        loadCurrentShelf();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        /*else if(id == R.id.action_search){
            return onSearchRequested();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_books) {
            currentShelfId = MY_BOOKS_SHELF_ID;
            loadCurrentShelf();
            getSupportActionBar().setTitle("My Books");
        }else if(id == R.id.signout){
            SignInActivity.signOut(MainActivity.this);
        }else if(id == R.id.create_shelf){
            //show create shelf dialog
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            DialogFragment newFragment = CreateShelfDialogFragment.newInstance();
            newFragment.show(ft, "dialog");
        }else{
            Shelf shelf = shelves.get(id);
            currentShelfId = shelf.getShelfId();
            loadCurrentShelf();
            getSupportActionBar().setTitle(shelf.getShelfName());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //called when activity is resumed eg when navigated to via back button
    @Override
    public void onResume(){
        super.onResume();
    }

    private void populateNavigationDrawer(){
        final Menu menu = navigationView.getMenu();
        Query query = mDatabase.child("shelves").orderByChild("userId").equalTo(currentUser.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                Shelf shelf = dataSnapshot.getValue(Shelf.class);
                shelves.add(shelf);
                menu.add(0, shelves.size()-1, 0, shelf.getShelfName()).setIcon(R.drawable.icon_custom_shelf);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load shelves.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //populate current books array with books corresponding to current shelf
    private void loadCurrentShelf(){
        //clear adapter
        booksList.setAdapter(null);
        adapter = new ShelfAdapter(this, new ArrayList<Book>(), new ArrayList<Book>());
        booksList.setAdapter(adapter);

        Query query = mDatabase.child("books").orderByChild("userShelfId").equalTo(currentUser.getUid() + currentShelfId);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Book book = dataSnapshot.getValue(Book.class);
                adapter.updateEntries(book);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load books.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
