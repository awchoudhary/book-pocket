package com.awchoudhary.bookpocket;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by awaeschoudhary on 4/15/17.
 */

public class UpdateDialogFragment extends DialogFragment {
    //codes for fragment args
    private static final String BOOK_KEY = "book_key";

    //the book that is being edited
    private Book book;

    public static UpdateDialogFragment newInstance(Book book) {
        UpdateDialogFragment dialog = new UpdateDialogFragment();

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

        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_update_reading_status, container, false);




        return v;
    }
}
