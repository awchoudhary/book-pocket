package com.awchoudhary.bookpocket;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static android.R.color.transparent;


/**
 * Created by awaeschoudhary on 4/1/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private Context context;
    private ArrayList<BookNote> notes;
    private final DateTimeHelper dateHelper;
    private boolean isActionMode; // indicates if action mode is active
    private ActionMode actionMode;

    //keeps track of selected cards
    private SparseBooleanArray selectedItems;

    public NotesAdapter(ArrayList<BookNote> notes, Context context){
        this.context = context;
        this.notes = notes;
        dateHelper = new DateTimeHelper();
        selectedItems = new SparseBooleanArray();
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
        DateTime date = note.getDate();
        String noteBody = note.getBody();

        //set the title and date view text
        if(!title.equals("") && date != null){
            //Title (date) format
            noteViewHolder.titleAndDateView.setText(title + " (" + dateHelper.toString(date) + ")");
        }
        else if(date == null){
            //just title
            noteViewHolder.titleAndDateView.setText(title);
        }
        else{
            //just date
            noteViewHolder.titleAndDateView.setText(dateHelper.toString(date));
        }

        noteViewHolder.noteBodyView.setText(noteBody);
        noteViewHolder.cv.setCardBackgroundColor(transparent);
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
                    //get dialog and set title
                    final Dialog dialog = new Dialog(context, R.style.NoteDialog);
                    dialog.setContentView(R.layout.dialog_new_note);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.note_dialog_background);
                    dialog.setTitle("Edit Note");

                    //populate fields with note values
                    ((EditText) dialog.findViewById(R.id.titleInput)).setText(note.getTitle());
                    ((EditText) dialog.findViewById(R.id.dateInput)).setText((dateHelper.toString(note.getDate())));
                    ((EditText) dialog.findViewById(R.id.noteInput)).setText(note.getBody());

                    //set date input as a date picker
                    EditText dateInput = (EditText) dialog.findViewById(R.id.dateInput);
                    new DatePickerCustom(context, dateInput);

                    //set event handler for save button
                    Button saveButton = (Button) dialog.findViewById(R.id.saveButton);
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateNote(note, dialog);

                            //refresh view
                            notifyDataSetChanged();

                            //hide dialog
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
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
        String title = ((EditText) dialog.findViewById(R.id.titleInput)).getText().toString();
        String date = ((EditText) dialog.findViewById(R.id.dateInput)).getText().toString();
        String body = ((EditText) dialog.findViewById(R.id.noteInput)).getText().toString();

        //update note item
        note.setTitle(title);
        if(!date.equals("")){
            note.setDate(dateHelper.toDateTime(date));
        }
        note.setBody(body);

        db.updateBookNote(note);
    }

    //remove or add index to the selectedItems list
    public void toggleSelection(int pos, CardView cardView) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            cardView.setCardBackgroundColor(transparent);
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
                        notes.remove(selectedItemPositions.get(i).intValue());
                    }
                    actionMode.finish();
                    notifyDataSetChanged();
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

}
