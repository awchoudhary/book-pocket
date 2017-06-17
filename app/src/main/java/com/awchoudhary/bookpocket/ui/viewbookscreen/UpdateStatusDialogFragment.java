package com.awchoudhary.bookpocket.ui.viewbookscreen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.mybooksscreen.Book;
import com.awchoudhary.bookpocket.util.DatabaseHandler;
import com.awchoudhary.bookpocket.util.DatePickerCustom;
import com.awchoudhary.bookpocket.util.DateTimeHelper;
import com.awchoudhary.bookpocket.util.ReadingStatus;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by awaeschoudhary on 4/15/17.
 */

public class UpdateStatusDialogFragment extends DialogFragment {
    //layout for the dialog
    private View dialogView;
    //codes for fragment args
    private static final String BOOK_KEY = "book_key";
    //the book that is being edited
    private Book book;
    //keeps track of selected radio button in the dialog
    private int selectedRadioID;
    DatabaseHandler db;

    public static UpdateStatusDialogFragment newInstance(Book book) {
        UpdateStatusDialogFragment dialog = new UpdateStatusDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(BOOK_KEY, book);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        book = (Book) getArguments().getSerializable(BOOK_KEY); // get Book being viewed
        db = new DatabaseHandler(getActivity());

        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteDialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Update Reading Status");
        dialogView = inflater.inflate(R.layout.dialog_update_reading_status, container, false);

        populate();

        //initialize date pickers
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_date_to_start_reading));
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_date_started_reading));
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_date_completed));

        //set event handlers for all dialog buttons
        RadioButton radioWantToRead = (RadioButton) dialogView.findViewById(R.id.radio_want_to_read);
        radioWantToRead.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onRadioButtonClicked(v);
            }
        });

        RadioButton radioStartedReading = (RadioButton) dialogView.findViewById(R.id.radio_start_reading);
        radioStartedReading.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onRadioButtonClicked(v);
            }
        });

        RadioButton radioCompleted= (RadioButton) dialogView.findViewById(R.id.radio_completed);
        radioCompleted.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onRadioButtonClicked(v);
            }
        });

        RadioButton radioClearStatus = (RadioButton) dialogView.findViewById(R.id.radio_clear_status);
        radioClearStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onRadioButtonClicked(v);
            }
        });

        Button saveButton = (Button) dialogView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook();
                Toast.makeText(getActivity(), "Reading Status Updated", Toast.LENGTH_SHORT).show();
            }
        });

        return dialogView;
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
                if (checked){
                    dialogView.findViewById(R.id.inputs_want_to_read).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_want_to_read;
                }
                break;
            case R.id.radio_start_reading:
                if (checked){
                    dialogView.findViewById(R.id.inputs_start_reading).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_start_reading;
                }
                break;
            case R.id.radio_completed:
                if (checked){
                    dialogView.findViewById(R.id.inputs_completed).setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.radio_completed;
                }
                break;
            case R.id.radio_clear_status:
                //No inputs to show here
                if(checked){
                    selectedRadioID = R.id.radio_clear_status;
                }
                break;
        }
    }

    //hide all inputs in the update status dialog
    private void hideUpdateDialogInputs(){
        dialogView.findViewById(R.id.inputs_want_to_read).setVisibility(View.GONE);
        dialogView.findViewById(R.id.inputs_start_reading).setVisibility(View.GONE);
        dialogView.findViewById(R.id.inputs_completed).setVisibility(View.GONE);
    }

    private void updateBook(){
        //set book properties according to selected status option
        switch (selectedRadioID){
            case R.id.radio_want_to_read:
                book.setReadingStatus(ReadingStatus.WANT_TO_READ.toString());

                String dateToReadBy = ((EditText)dialogView.findViewById(R.id.input_date_to_start_reading))
                        .getText().toString();
                String priorityString = ((EditText)dialogView.findViewById(R.id.input_priority_number))
                        .getText().toString();

                if(!priorityString.equals("")){
                    book.setSeqNo(Integer.parseInt(priorityString));
                }
                book.setDateToReadBy(dateToReadBy);
                break;

            case R.id.radio_start_reading:
                book.setReadingStatus(ReadingStatus.READING.toString());

                String dateStarted = ((EditText)dialogView.findViewById(R.id.input_date_started_reading))
                        .getText().toString();
                String currentPageString = ((EditText)dialogView.findViewById(R.id.input_current_page))
                        .getText().toString();

                if(!currentPageString.equals("")){
                    book.setCurrentPage(Integer.parseInt(currentPageString));
                }
                book.setDateStarted(dateStarted);
                break;

            case R.id.radio_completed:
                book.setReadingStatus(ReadingStatus.COMPLETED.toString());

                String dateCompleted = ((EditText)dialogView.findViewById(R.id.input_date_completed))
                        .getText().toString();

                book.setDateCompleted(dateCompleted);
                break;

            case R.id.radio_clear_status:
                book.setReadingStatus(ReadingStatus.NO_STATUS.toString());
                break;
        }

        //update the book
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("books").child(book.getId()).setValue(book);

        //hide dialog
        dismiss();
    }

    private void populate(){
        if(book.getReadingStatus() != null){
            //populate dialog based on book's reading status
            if(book.getReadingStatus().equals(ReadingStatus.WANT_TO_READ.toString())){
                //populate fields
                ((EditText)dialogView.findViewById(R.id.input_date_to_start_reading))
                        .setText(book.getDateToReadBy());
                //populate fields
                ((EditText)dialogView.findViewById(R.id.input_priority_number))
                        .setText(Integer.toString(book.getSeqNo()));
            }else if(book.getReadingStatus().equals(ReadingStatus.READING.toString())){
                //populate fields
                ((EditText)dialogView.findViewById(R.id.input_date_started_reading))
                        .setText(book.getDateStarted());
                //populate fields
                ((EditText)dialogView.findViewById(R.id.input_current_page))
                        .setText(Integer.toString(book.getCurrentPage()));
            }else if(book.getReadingStatus().equals(ReadingStatus.COMPLETED.toString())){
                //populate fields
                ((EditText)dialogView.findViewById(R.id.input_date_completed))
                        .setText(book.getDateCompleted());
            }
        }
    }

}
