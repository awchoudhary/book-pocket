package com.awchoudhary.bookpocket;

import android.app.Dialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesTabFragment extends Fragment{
    //the book that is being edited
    private Book book;

    //codes for intents
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

        //initialize recycle view and set adapter
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        final NotesAdapter adapter = new NotesAdapter(notes, getActivity());
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(true);


        FloatingActionMenu fabMenu = (FloatingActionMenu) view.findViewById(R.id.fab_notes_tab);
        fabMenu.setClosedOnTouchOutside(true);

        //set event handler for new note button
        FloatingActionButton newNoteButton = (FloatingActionButton) view.findViewById(R.id.createNoteButton);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get dialog and set title
                final Dialog dialog = new Dialog(getActivity(), R.style.NoteDialog);
                dialog.setContentView(R.layout.dialog_new_note);
                dialog.setTitle("New Note");
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.note_dialog_background);

                //set date input as a date picker
                EditText dateInput = (EditText) dialog.findViewById(R.id.dateInput);
                new DatePickerCustom(getActivity(), dateInput);

                //set event handler for save button
                Button saveButton = (Button) dialog.findViewById(R.id.saveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BookNote note = createNote(dialog);

                        //add new book to adapter
                        adapter.updateEntries(note);

                        //hide dialog
                        dialog.dismiss();
                    }
                });
                dialog.show();
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



}
