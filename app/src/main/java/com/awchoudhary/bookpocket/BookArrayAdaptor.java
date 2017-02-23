package com.awchoudhary.bookpocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get fields within row
        View rowView = inflater.inflate(R.layout.content_main, parent, false);
        ImageView cover = (ImageView) rowView.findViewById(R.id.coverImage);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView author = (TextView) rowView.findViewById(R.id.author);

        //set text values.
        title.setText(books.get(position).getName());
        author.setText(books.get(position).getAuthor());

        //download image into imageview
        Picasso.with(context)
                .load(books.get(position).getCoverUrl())
                .placeholder(R.drawable.sabriel_cover)
                .error(R.drawable.sample_cover)
                .into(cover);

        return rowView;
    }
}
