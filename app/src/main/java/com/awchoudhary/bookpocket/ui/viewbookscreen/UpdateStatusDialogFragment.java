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
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_date_started_reading));
        new DatePickerCustom(getActivity(), (EditText) dialogView.findViewById(R.id.input_date_completed));

        //set event handlers for all dialog buttons
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


    private void updateBook(){
        //update book with dates
        String dateStarted = ((EditText)dialogView.findViewById(R.id.input_date_started_reading))
                .getText().toString();
        book.setDateStarted(dateStarted);

        String dateCompleted = ((EditText)dialogView.findViewById(R.id.input_date_completed))
                .getText().toString();

        book.setDateCompleted(dateCompleted);

        //update the book
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("books").child(book.getId()).setValue(book);

        //hide dialog
        dismiss();
    }

    private void populate(){
        if(book.getDateStarted() != null && !book.getDateStarted().equals("")){
            //populate fields
            ((EditText)dialogView.findViewById(R.id.input_date_started_reading))
                    .setText(book.getDateStarted());
        }
        if(book.getDateCompleted() != null && !book.getDateCompleted().equals("")){
            //populate fields
            ((EditText)dialogView.findViewById(R.id.input_date_completed))
                    .setText(book.getDateCompleted());
        }
    }

}
