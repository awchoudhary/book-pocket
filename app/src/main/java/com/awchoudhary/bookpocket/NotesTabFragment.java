package com.awchoudhary.bookpocket;

import android.app.Dialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesTabFragment extends Fragment{
    //dialogs used in fragment
    private Dialog createNoteDialog;

    //the book that is being edited
    private Book book;

    //codes for fragment args
    private static final String BOOK_KEY = "book_key";

    public static NotesTabFragment newInstance(Book book) {
        NotesTabFragment fragment = new NotesTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BOOK_KEY, book);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_tab_fragment, container, false);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        book = (Book) getArguments().getSerializable(BOOK_KEY); // get Book to be Edited

        //get all notes for the book
        ArrayList<BookNote> notes = db.getBookNotes(book.getId());

        updateStatusBox(view);

        //initialize recycle view and set adapter
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final NotesAdapter adapter = new NotesAdapter(notes, getActivity());
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(true);


        FloatingActionMenu fabMenu = (FloatingActionMenu) view.findViewById(R.id.fab_notes_tab);
        fabMenu.setClosedOnTouchOutside(true);

        //set event handlers for fab buttons
        FloatingActionButton newNoteButton = (FloatingActionButton) view.findViewById(R.id.createNoteButton);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get dialog and set title
                createNoteDialog = new Dialog(getActivity(), R.style.NoteDialog);
                createNoteDialog.setContentView(R.layout.dialog_new_note);
                createNoteDialog.setTitle("New Note");
                createNoteDialog.getWindow().setBackgroundDrawableResource(R.drawable.note_dialog_background);

                //set date input as a date picker
                EditText dateInput = (EditText) createNoteDialog.findViewById(R.id.dateInput);
                new DatePickerCustom(getActivity(), dateInput);

                //set event handler for save button
                Button saveButton = (Button) createNoteDialog.findViewById(R.id.saveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BookNote note = createNote(createNoteDialog);

                        //add new book to adapter
                        adapter.updateEntries(note);

                        //hide dialog
                        createNoteDialog.dismiss();
                    }
                });
                createNoteDialog.show();
            }
        });

        final FloatingActionButton updateStatusButton = (FloatingActionButton) view.findViewById(R.id.updateReadingStatusButton);
        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //create and show update dialog
                UpdateStatusDialogFragment dialog = new UpdateStatusDialogFragment();
                showDialog(book);
            }
        });

        return view;
    }


    //save note in db
    private BookNote createNote(Dialog dialog){
        DateTimeHelper dateHelper = new DateTimeHelper();
        //get all text inputs
        String title = ((EditText) dialog.findViewById(R.id.titleInput)).getText().toString();
        String date = ((EditText) dialog.findViewById(R.id.dateInput)).getText().toString();
        String body = ((EditText) dialog.findViewById(R.id.noteInput)).getText().toString();

        //populate new book object with inputs
        BookNote note = new BookNote();
        note.setBookId(book.getId());

        //only set date if non-empty, or the datehelper will return current date which we don't want
        if(!date.equals("")){
            note.setDate(dateHelper.toDateTime(date));
        }
        note.setTitle(title);
        note.setBody(body);

        //write to db
        DatabaseHandler db = new DatabaseHandler(dialog.getContext());
        db.createBookNote(note);

        return note;
    }


    //show update status dialog
    private void showDialog(Book book) {
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
    }


    private void updateStatusBox(View view){
        if(book.getReadingStatus() != null){
            TextView statusText = (TextView) view.findViewById(R.id.status_text);

            if(book.getReadingStatus().equals(ReadingStatus.WANT_TO_READ.toString())){
                statusText.setText("To Start on " + DateTimeHelper.toString(book.getDateToReadBy()));
            }else if(book.getReadingStatus().equals(ReadingStatus.READING.toString())){
                statusText.setText("Started on " + DateTimeHelper.toString(book.getDateStarted()));
            }else if(book.getReadingStatus().equals(ReadingStatus.COMPLETED.toString())){
                statusText.setText("Completed on " + DateTimeHelper.toString(book.getDateCompleted()));
            }else if(book.getReadingStatus().equals(ReadingStatus.NO_STATUS.toString())){
                statusText.setText("Update Reading Status Using Bottom Button");
                statusText.setTextColor(getResources().getColor(R.color.light_grey));
            }
        }
    }

}
