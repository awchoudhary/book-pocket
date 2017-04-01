package com.awchoudhary.bookpocket;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class EditNotesTabFragment extends Fragment{
    private DatabaseHandler db;
    Book book; //Book being edited
    ArrayList<BookNote> notes; // Notes being edited
    ListView notesListView;
    NotesListAdapter adapter;

    //codes for intents
    private static final String BOOK_KEY = "book_key";

    public static EditNotesTabFragment newInstance(Book book) {
        EditNotesTabFragment fragment = new EditNotesTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BOOK_KEY, book);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        View view = inflater.inflate(R.layout.edit_notes_tab_fragment, container, false);
        book = (Book) getArguments().getSerializable(BOOK_KEY); // get Book to be Edited

        //populate listview with notes for book from db
        notesListView = (ListView) view.findViewById(R.id.notesList);
        notes = db.getBookNotes(book.getId());
        if(notes == null || notes.size() == 0){
            notes = new ArrayList<BookNote>();
            notes.add(newBookNote());
        }
        adapter = new NotesListAdapter(getActivity(), notes, book);
        notesListView.setAdapter(adapter);

        return view;
    }

    public void saveNotes(){
        //get the notes that are to be saved
        ArrayList<BookNote> notesToSave = new ArrayList<BookNote>();
        for(int i = 0; i < adapter.getCount(); i++){
            notesToSave.add((BookNote)notesListView.getItemAtPosition(i));
        }

        db.saveBookNotes(notesToSave);
    }

    //create new book with book id
    private BookNote newBookNote(){
        BookNote note = new BookNote();
        note.setBookId(book.getId());
        return note;
    }

}
