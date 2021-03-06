package com.awchoudhary.bookpocket.ui.viewbookscreen;

import android.app.Dialog;
import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.mybooksscreen.NoteDialogFragment;
import com.awchoudhary.bookpocket.util.ReadingStatus;
import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;
import com.awchoudhary.bookpocket.util.DatabaseHandler;
import com.awchoudhary.bookpocket.util.DateTimeHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesTabFragment extends Fragment{
    //view for activity
    private View tabLayout;

    //the book that is being edited
    private Book book;

    //codes for fragment args
    private static final String BOOK_KEY = "book_key";

    private NotesAdapter adapter;
    private RecyclerView notesRecycleView;
    private DatabaseReference mDatabase;


    public static NotesTabFragment newInstance(Book book) {
        NotesTabFragment fragment = new NotesTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BOOK_KEY, book);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tabLayout = inflater.inflate(R.layout.notes_tab_fragment, container, false);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        book = (Book) getArguments().getSerializable(BOOK_KEY); // get Book to be Edited

        //get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        displayStatusCards(tabLayout);

        //initialize recycle view and set adapter
        notesRecycleView = (RecyclerView) tabLayout.findViewById(R.id.rv);
        notesRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NotesAdapter(new ArrayList<BookNote>(), book.getId(), getActivity());
        notesRecycleView.setAdapter(adapter);
        notesRecycleView.setNestedScrollingEnabled(true);

        loadNotesForBook();


        FloatingActionMenu fabMenu = (FloatingActionMenu) tabLayout.findViewById(R.id.fab_notes_tab);
        fabMenu.setClosedOnTouchOutside(true);

        //set event handlers for fab buttons
        FloatingActionButton newNoteButton = (FloatingActionButton) tabLayout.findViewById(R.id.createNoteButton);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog(book.getId());
            }
        });

        final FloatingActionButton updateStatusButton = (FloatingActionButton) tabLayout.findViewById(R.id.updateReadingStatusButton);
        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //create and show update dialog
                UpdateStatusDialogFragment dialog = new UpdateStatusDialogFragment();
                showUpdateStatusDialog(book);
            }
        });

        return tabLayout;
    }

    //show update status dialog
    private void showNoteDialog(String bookId) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        NoteDialogFragment noteDialog = NoteDialogFragment.newInstance(null, bookId);
        noteDialog.show(ft, "dialog");
    }

    //show update status dialog
    private void showUpdateStatusDialog(Book book) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = UpdateStatusDialogFragment.newInstance(book);
        newFragment.show(ft, "dialog");

        //set dismiss handler
        getFragmentManager().executePendingTransactions();
        newFragment.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                displayStatusCards(tabLayout);
            }
        });
    }

    private void displayStatusCards(View view){

        //show or hide date started reading
        if(book.getDateStarted() != null && !book.getDateStarted().equals("")){
            ((TextView) (view.findViewById(R.id.text_status_started))).setText("Started on " + book.getDateStarted());
            view.findViewById(R.id.card_view_date_started).setVisibility(View.VISIBLE);
        }
        else{
            view.findViewById(R.id.card_view_date_started).setVisibility(View.GONE);
        }

        //show or hide date completed
        if(book.getDateCompleted() != null && !book.getDateCompleted().equals("")){
            ((TextView) (view.findViewById(R.id.text_status_completed))).setText("Completed on " + book.getDateCompleted());
            view.findViewById(R.id.card_view_date_completed).setVisibility(View.VISIBLE);
        }
        else{
            view.findViewById(R.id.card_view_date_completed).setVisibility(View.GONE);
        }
    }

    private void loadNotesForBook(){
        //clear adapter
        notesRecycleView.setAdapter(null);
        adapter = new NotesAdapter(new ArrayList<BookNote>(), book.getId(), getActivity());
        notesRecycleView.setAdapter(adapter);

        Query query = mDatabase.child("notes").orderByChild("bookId").equalTo(book.getId());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new comment has been added, add it to the displayed list
                BookNote note = dataSnapshot.getValue(BookNote.class);
                adapter.updateEntries(note);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                adapter.notifyItemChanged(adapter.getRecentSelectedIndex());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load notes.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
