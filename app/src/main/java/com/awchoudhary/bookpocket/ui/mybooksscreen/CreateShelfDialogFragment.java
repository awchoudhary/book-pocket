package com.awchoudhary.bookpocket.ui.mybooksscreen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by awaeschoudhary on 5/5/17.
 */

public class CreateShelfDialogFragment extends DialogFragment {
    private DatabaseReference mDatabase;

    public static CreateShelfDialogFragment newInstance() {
        CreateShelfDialogFragment dialog = new CreateShelfDialogFragment();

        //supply args here

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoteDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Create Shelf");
        View dialogView = inflater.inflate(R.layout.dialog_create_shelf, container, false);

        //get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set event handlers for all dialog buttons
        Button saveButton = (Button) dialogView.findViewById(R.id.button_save_shelf);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                dismiss();
                Toast.makeText(getActivity(), "Created New Shelf", Toast.LENGTH_SHORT).show();
            }
        });

        return dialogView;
    }

    private void save(){
        View view = getView();

        //get unique id for shelf
        String shelfId = mDatabase.child("shelves").push().getKey();

        //get current userId
        String userId = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(getActivity(), "Error: No signed in user found", Toast.LENGTH_SHORT).show();
            return;
        }

        //create new shelf with values from dialog
        Shelf shelf = new Shelf(shelfId, ((EditText)(view.findViewById(R.id.input_shelf_name))).getText().toString()
                                , userId);

        //save new shelf
        mDatabase.child("shelves").child(shelfId).setValue(shelf);
    }
}
