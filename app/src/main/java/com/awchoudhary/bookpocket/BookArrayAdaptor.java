package com.awchoudhary.bookpocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 2/21/17.
 * Custom array adaptor for book objects
 */
public class BookArrayAdaptor extends ArrayAdapter<Book> {
    private final Context context;
    private final ArrayList<Book> books;

    public BookArrayAdaptor(Context context, ArrayList<Book> books) {
        super(context, R.layout.content_main, books);
        this.context = context;
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        Book book = books.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get fields within row
        View rowView = inflater.inflate(R.layout.content_main, parent, false);
        ImageView cover = (ImageView) rowView.findViewById(R.id.coverImage);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView author = (TextView) rowView.findViewById(R.id.author);
        TextView date = (TextView) rowView.findViewById(R.id.date);

        //set text values.
        title.setText(book.getName());
        author.setText(book.getAuthor());

        //download image into imageview.
        Glide.with(context)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.default_cover_image)
                .error(R.drawable.default_cover_image)
                .override(131, 200) //TODO: Needs to be responsive
                .into(cover);

        //if book has a start and end date (completed), display the date completed.
        if(book.getDateStarted() != null && book.getDateCompleted() != null){
            date.setText("Completed on " + dateFormatter.print(book.getDateCompleted()));
            date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_completed, 0, 0, 0);
        }

        //if book is in progress, display the date started
        if(book.getDateStarted() != null && book.getDateCompleted() == null){
            date.setText("Started on " + dateFormatter.print(book.getDateStarted()));
            date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_reading, 0, 0, 0);
        }

        //set background for row (those swank borders)
        rowView.setBackgroundResource(R.drawable.list_item_background);

        return rowView;
    }
}
