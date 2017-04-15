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
import android.widget.RadioButton;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesTabFragment extends Fragment{
    //dialogs used in fragment
    private Dialog createNoteDialog;
    private Dialog updateStatusDialog;


    //the book that is being edited
    private Book book;

    //codes for fragment args
    private static final String BOOK_KEY = "book_key";

    //keeps track of selected radio button in the update status dialog
    private int selectedRadioID;

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
                //get dialog and set title
                updateStatusDialog = new Dialog(getActivity(), R.style.NoteDialog);
                updateStatusDialog.setContentView(R.layout.dialog_update_reading_status);
                updateStatusDialog.setTitle("Update Reading Status");
                updateStatusDialog.getWindow().setBackgroundDrawableResource(R.drawable.note_dialog_background);

                //set event handlers for all dialog buttons
                RadioButton radioWantToRead = (RadioButton) updateStatusDialog.findViewById(R.id.radio_want_to_read);
                radioWantToRead.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        onRadioButtonClicked(v);
                    }
                });

                RadioButton radioStartedReading = (RadioButton) updateStatusDialog.findViewById(R.id.radio_start_reading);
                radioStartedReading.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        onRadioButtonClicked(v);
                    }
                });

                RadioButton radioCompleted= (RadioButton) updateStatusDialog.findViewById(R.id.radio_completed);
                radioCompleted.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        onRadioButtonClicked(v);
                    }
                });

                RadioButton radioClearStatus = (RadioButton) updateStatusDialog.findViewById(R.id.radio_clear_status);
                radioClearStatus.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        onRadioButtonClicked(v);
                    }
                });

                Button saveButton = (Button) updateStatusDialog.findViewById(R.id.saveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (selectedRadioID){
                            case R.id.radio_want_to_read:
                                String dateToReadBy = ((EditText)updateStatusDialog.findViewById(R.id.input_date_to_start_reading))
                                                        .getText().toString();
                                int priority = Integer.parseInt(((EditText)updateStatusDialog.findViewById(R.id.input_priority_number))
                                                .getText().toString());
                                book.setDateToReadBy(DateTimeHelper.toDateTime(dateToReadBy));
                                book.setSeqNo(priority);

                            case R.id.radio_start_reading:
                                String dateStarted = ((EditText)updateStatusDialog.findViewById(R.id.input_date_started))
                                        .getText().toString();
                                int currentPage = Integer.parseInt(((EditText)updateStatusDialog.findViewById(R.id.input_current_page))
                                        .getText().toString());
                                book.setDateToReadBy(DateTimeHelper.toDateTime(dateToReadBy));
                                book.setCurrentPage(currentPage);

                            case R.id.radio_completed:
                                String dateStarted = ((EditText)updateStatusDialog.findViewById(R.id.input_date_started))
                                        .getText().toString();

                            case R.id.radio_clear_status:
                                book.setReadingStatus(ReadingStatus.NO_STATUS.toString());
                        }
                    }
                });


                updateStatusDialog.show();
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

    //on click handlers for update status dialog radio buttons
    public void onRadioButtonClicked(View view){
        //first of all, hide all inputs
        hideUpdateDialogInputs();

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // show the inputs for the selected radio button
        switch(view.getId()) {
            case R.id.radio_want_to_read:
                if (checked)
                    updateStatusDialog.findViewById(R.id.inputs_want_to_read).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_want_to_read;
                    break;
            case R.id.radio_start_reading:
                if (checked)
                    updateStatusDialog.findViewById(R.id.inputs_start_reading).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_start_reading;
                    break;
            case R.id.radio_completed:
                if (checked)
                    updateStatusDialog.findViewById(R.id.inputs_completed).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_completed;
                break;
            case R.id.radio_clear_status:
                //No inputs to show here
                selectedRadioID = R.id.radio_clear_status;
                break;
        }
    }

    //hide all inputs in the update status dialog
    private void hideUpdateDialogInputs(){
        updateStatusDialog.findViewById(R.id.inputs_want_to_read).setVisibility(View.GONE);
        updateStatusDialog.findViewById(R.id.inputs_start_reading).setVisibility(View.GONE);
        updateStatusDialog.findViewById(R.id.inputs_completed).setVisibility(View.GONE);
    }



}
