package com.awchoudhary.bookpocket.ui.viewbookscreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.mybooksscreen.NoteDialogFragment;
import com.awchoudhary.bookpocket.util.DatabaseHandler;
import com.awchoudhary.bookpocket.util.DatePickerCustom;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by awaeschoudhary on 4/1/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private DatabaseReference mDatabase;
    private Context context;
    private ArrayList<BookNote> notes;
    private boolean isActionMode; // indicates if action mode is active
    private ActionMode actionMode;
    private DatabaseHandler db;
    private String bookId;
    private int recentSelectedIndex = 0; // The index in the notes array for the most recently selected item

    //keeps track of selected cards
    private SparseBooleanArray selectedItems;

    public NotesAdapter(ArrayList<BookNote> notes, String bookId, Context context){
        this.context = context;
        this.notes = notes;
        this.bookId = bookId;
        selectedItems = new SparseBooleanArray();
        db = new DatabaseHandler(context);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
        NoteViewHolder nvh = new NoteViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder noteViewHolder, final int position) {
        BookNote note = notes.get(position);
        String title = note.getTitle();
        String date = note.getDate();
        String noteBody = note.getBody();

        //set the title and date view text
        if(!title.equals("") && date != null){
            //Title (date) format
            noteViewHolder.titleAndDateView.setText(title + " (" + date + ")");
        }
        else if(date == null){
            //just title
            noteViewHolder.titleAndDateView.setText(title);
        }
        else if(title.equals("")){
            //just date
            noteViewHolder.titleAndDateView.setText(date);
        }
        else{
            noteViewHolder.titleAndDateView.setVisibility(View.GONE);
        }

        noteViewHolder.noteBodyView.setText(noteBody);
        noteViewHolder.cv.setCardBackgroundColor(Color.parseColor("#ffffff"));
        noteViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            BookNote note = notes.get(position);
            @Override
            public void onClick(View v) {
                if(isActionMode){
                    //add or remove item from selected list
                    toggleSelection(position, noteViewHolder.cv);

                    //update title for action mode
                    actionMode.setTitle(Integer.toString(getSelectedItemCount()));
                }

                else{
                    // Create and show the dialog.
                    FragmentTransaction ft = ((TabManagerActivity)context).getSupportFragmentManager().beginTransaction();
                    NoteDialogFragment noteDialog = NoteDialogFragment.newInstance(notes.get(noteViewHolder.getAdapterPosition()), bookId);
                    noteDialog.show(ft, "dialog");

                    //update recentSelectedIndex
                    recentSelectedIndex = noteViewHolder.getAdapterPosition();
                }
            }
        });

        //start actionmode activity on long press for mass delete
        noteViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isActionMode = true;

                actionMode = ((TabManagerActivity)context).startSupportActionMode(new ActionModeCallback());

                if(actionMode == null){
                    return false;
                }

                //add or remove item from selected list
                toggleSelection(position, noteViewHolder.cv);

                //update title for action mode
                actionMode.setTitle(Integer.toString(getSelectedItemCount()));

                return true;
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateEntries(BookNote note) {
        notes.add(note);
        notifyDataSetChanged();
    }

    private void updateNote(BookNote note, Dialog dialog){
        DatabaseHandler db = new DatabaseHandler(context);

        //get all text inputs
        String title = ((EditText) dialog.findViewById(R.id.input_note_title)).getText().toString();
        String date = ((EditText) dialog.findViewById(R.id.input_note_date)).getText().toString();
        String body = ((EditText) dialog.findViewById(R.id.input_note_body)).getText().toString();

        //update note item
        note.setTitle(title);
        if(!date.equals("")){
            note.setDate(date);
        }
        note.setBody(body);

        db.updateBookNote(note);
    }

    //remove or add index to the selectedItems list
    public void toggleSelection(int pos, CardView cardView) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }
        else {
            selectedItems.put(pos, true);
            cardView.setCardBackgroundColor(Color.parseColor("#e6e6e6"));
        }
    }

    //remove all indexes from selected items
    public void clearSelections() {
        selectedItems.clear();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    //return list of selected indexes
    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public int getRecentSelectedIndex(){
        return recentSelectedIndex;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView titleAndDateView;
        TextView noteBodyView;

        NoteViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            titleAndDateView = (TextView)itemView.findViewById(R.id.titleAndDateView);
            noteBodyView = (TextView)itemView.findViewById(R.id.noteBody);
        }
    }

    //actionmode callback for longpress
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.note_action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    //remove all selected items from the list
                    List<Integer> selectedItemPositions = getSelectedItems();
                    for (int i = selectedItemPositions.size()-1; i >= 0; i--) {
                        final int position = selectedItemPositions.get(i);
                        BookNote note = notes.get(position);

                        mDatabase.child("notes").child(note.getNoteId()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                notes.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
                    }

                    //display confirmation message
                    showMessage(getSelectedItemCount() + " notes deleted.");

                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            isActionMode = false;

            //a hacky way to remove highlighting from rows since row color is set to transparent on binding
            notifyDataSetChanged();

            //unselect all items
            clearSelections();
        }
    }

    //display toast
    private void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
