package com.awchoudhary.bookpocket.ui.mybooksscreen;

import android.support.v4.app.DialogFragment;;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.viewbookscreen.BookNote;
import com.awchoudhary.bookpocket.util.DatePickerCustom;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by awaeschoudhary on 5/8/17.
 */

public class NoteDialogFragment extends DialogFragment{
    //codes for bundle args
    private static final String NOTE = "noteKey";
    private static final String BOOK_ID = "bookIdKey";

    private DatabaseReference mDatabase;
    private boolean isEdit;
    private String bookId;
    private BookNote note;

    public static NoteDialogFragment newInstance(BookNote note, String bookId) {
        NoteDialogFragment dialog = new NoteDialogFragment();

        //supply args here
        Bundle args = new Bundle();
        args.putString(BOOK_ID, bookId);
        args.putSerializable(NOTE, note);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookId = getArguments().getString(BOOK_ID);
        note = (BookNote) getArguments().getSerializable(NOTE);

        //if noteId is null, we are creating a new note
        isEdit = (note == null) ? false : true;

        //get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.NoteDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set title
        getDialog().setTitle((isEdit) ? "Edit Note" : "New Note");

        //inflate view
        View dialogView = inflater.inflate(R.layout.dialog_note, container, false);

        //if we are editing a note, populate dialog
        if(isEdit){
            populate(note);
        }

        //initialize date pickers
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_note_date));

        //set event handlers for all dialog buttons
        Button saveButton = (Button) dialogView.findViewById(R.id.button_save_note);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save((isEdit) ? note.getNoteId() : null);
                dismiss();
                Toast.makeText(getActivity(), (isEdit) ? "Note Updated" : "New Note Created", Toast.LENGTH_SHORT).show();
            }
        });

        return dialogView;
    }

    private void populate(BookNote note){
        View view = getView();

        ((EditText)(view.findViewById(R.id.input_note_title))).setText(note.getTitle());
        ((EditText)(view.findViewById(R.id.input_note_date))).setText(note.getDate());
        ((EditText)(view.findViewById(R.id.input_note_body))).setText(note.getBody());
    }

    private void save(String noteId){
        View view = getView();

        //get unique id for note if we are not editing an existing note
        if(!isEdit){
            noteId = mDatabase.child("notes").push().getKey();
        }

        //create note object
        BookNote note = new BookNote(noteId, bookId
                            ,((EditText)(view.findViewById(R.id.input_note_title))).getText().toString()
                            ,((EditText)(view.findViewById(R.id.input_note_date))).getText().toString()
                            ,((EditText)(view.findViewById(R.id.input_note_body))).getText().toString());

        //create or update new note
        mDatabase.child("notes").child(noteId).setValue(note);
    }
}
