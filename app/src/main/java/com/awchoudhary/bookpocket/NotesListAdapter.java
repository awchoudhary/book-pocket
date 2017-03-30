package com.awchoudhary.bookpocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.attr.visible;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesListAdapter extends ArrayAdapter<BookNote> {
    private final Context context;
    private ArrayList<BookNote> notes;
    private Book book;

    public NotesListAdapter(Context context, ArrayList<BookNote> notes, Book book) {
        super(context, R.layout.edit_notes_tab_fragment, notes);
        this.context = context;
        this.notes = notes;
        this.book = book;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DateTimeHelper dateTimeHelper = new DateTimeHelper();
        BookNote note = notes.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get fields within row
        View rowView = inflater.inflate(R.layout.edit_notes_tab_fragment, parent, false);
        LinearLayout form = (LinearLayout) rowView.findViewById(R.id.newNoteForm);
        TextView titleInput = (TextView) rowView.findViewById(R.id.titleInput);
        TextView dateInput = (TextView) rowView.findViewById(R.id.dateInput);
        TextView noteInput = (TextView) rowView.findViewById(R.id.noteInput);

        //populate fields
        titleInput.setText(note.getTitle());
        if (note.getDate() != null) { //if date is null, the current date is returned. Probably a jodatime thing.
            dateInput.setText(dateTimeHelper.toString(note.getDate()));
        }
        noteInput.setText(note.getBody());

        //set event handlers
        final TextView newNoteButton = (TextView) rowView.findViewById(R.id.newNoteButton);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a new empty note to list
                BookNote note = new BookNote();
                note.setBookId(book.getId());
                notes.add(note);
                updateEntries(notes);
                newNoteButton.setVisibility(View.GONE);
            }
        });

        form.setVisibility(View.VISIBLE);

        return rowView;
    }

    public void updateEntries(ArrayList<BookNote> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }
}
