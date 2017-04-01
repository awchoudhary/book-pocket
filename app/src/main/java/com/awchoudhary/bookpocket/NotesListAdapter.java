package com.awchoudhary.bookpocket;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.title;
import static android.R.attr.visible;

/**
 * Created by awaeschoudhary on 3/26/17.
 */

public class NotesListAdapter extends ArrayAdapter<BookNote> {
    private DateTimeHelper dateTimeHelper = new DateTimeHelper();
    private boolean firstLoad = true;
    private final Context context;
    private ArrayList<BookNote> notes;
    private Book book;
    int viewPositon; //keeps track of position of visible notes in list
    //These maps keep track of text view content as they become invisible on scroll.
    private HashMap<Integer, String> titleTextMap;
    private HashMap<Integer, String> dateTextMap;
    private HashMap<Integer, String> bodyTextMap;

    //textwatchers to update maps when text views are updated
    private TextWatcher titleTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void afterTextChanged(Editable s) {
            titleTextMap.put(viewPositon, s.toString());
            notes.get(viewPositon).setTitle(s.toString());
        }
    };
    private TextWatcher dateTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void afterTextChanged(Editable s) {
            dateTextMap.put(viewPositon, s.toString());
            notes.get(viewPositon).setDate(dateTimeHelper.toDateTime(s.toString()));
        }
    };
    private TextWatcher bodyTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void afterTextChanged(Editable s) {
            bodyTextMap.put(viewPositon, s.toString());
            notes.get(viewPositon).setBody(s.toString());
        }
    };

    public NotesListAdapter(Context context, ArrayList<BookNote> notes, Book book) {
        super(context, R.layout.edit_notes_tab_fragment, notes);
        this.context = context;
        this.notes = notes;
        this.book = book;
        this.titleTextMap = new HashMap<Integer, String>();
        this.dateTextMap = new HashMap<Integer, String>();
        this.bodyTextMap = new HashMap<Integer, String>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewPositon = position;
        ViewHolder holder;
        BookNote note = notes.get(position);

        if(convertView == null){
            //get row and create holder object for it
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.edit_notes_tab_fragment, null);
            holder.form = (LinearLayout) convertView.findViewById(R.id.newNoteForm);
            holder.titleInput = (TextView) convertView.findViewById(R.id.titleInput);
            holder.dateInput = (TextView) convertView.findViewById(R.id.dateInput);
            holder.bodyInput = (TextView) convertView.findViewById(R.id.noteInput);
            holder.newNoteButton = (TextView) convertView.findViewById(R.id.newNoteButton);

            //store the holder object to reuse later
            convertView.setTag(holder);
        }
        else{
            //get the holder for the row
            holder = (ViewHolder) convertView.getTag();;
        }

        /*if row is being loaded for the very first time, update the maps with
        * the contents of the note at the row position*/
        if(firstLoad){
            titleTextMap.put(viewPositon, note.getTitle());
            dateTextMap.put(viewPositon, dateTimeHelper.toString(note.getDate()));
            bodyTextMap.put(viewPositon, note.getBody());
        }

        //populate fields
        holder.titleInput.setText(titleTextMap.get(viewPositon));
        String noteDate = dateTextMap.get(viewPositon);
        if (noteDate != null && noteDate.equals("")) { //if date is null, the current date is returned. Probably a jodatime thing.
            holder.dateInput.setText(dateTextMap.get(viewPositon));
        }
        holder.bodyInput.setText(bodyTextMap.get(viewPositon));

        //set event handlers
        holder.newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a new empty note to list
                BookNote note = new BookNote();
                note.setBookId(book.getId());
                notes.add(note);
                updateEntries(notes);
            }
        });

        //set text watchers for inputs
        holder.titleInput.addTextChangedListener(titleTextWatcher);
        holder.dateInput.addTextChangedListener(dateTextWatcher);
        holder.bodyInput.addTextChangedListener(bodyTextWatcher);

        //show the list which is invisible by default.
        holder.form.setVisibility(View.VISIBLE);

        //set firstLoad to false if this is the last row being rendered
        if(firstLoad && position == notes.size()-1){
            firstLoad = false;
        }

        return convertView;
    }

    public void updateEntries(ArrayList<BookNote> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    //object to wrap all views for a note row
    private class ViewHolder{
        LinearLayout form;
        TextView titleInput;
        TextView dateInput;
        TextView bodyInput;
        TextView newNoteButton;
    }
}
