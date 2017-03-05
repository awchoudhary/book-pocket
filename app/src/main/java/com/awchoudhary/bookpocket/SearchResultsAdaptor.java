package com.awchoudhary.bookpocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 2/23/17.
 */

public class SearchResultsAdaptor extends BaseAdapter{
    private final Context context;
    private ArrayList<Book> books = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public SearchResultsAdaptor(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get fields within row
        View rowView = layoutInflater.inflate(R.layout.search_results_list, parent, false);
        ImageView cover = (ImageView) rowView.findViewById(R.id.coverImage);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView subtitle = (TextView) rowView.findViewById(R.id.subtitle);
        TextView author = (TextView) rowView.findViewById(R.id.author);

        //set text values.
        title.setText(books.get(position).getName());
        subtitle.setText("(" + books.get(position).getSubtitle() + ")");
        author.setText(books.get(position).getAuthor());

        //download image into imageview
        Picasso.with(context)
                .load(books.get(position).getCoverUrl())
                .placeholder(R.drawable.default_cover_image)
                .error(R.drawable.default_cover_image)
                .into(cover);

        //if the subtitle is null, hide the textview for it
        if(books.get(position).getSubtitle() == null || books.get(position).getSubtitle().equals("")){
            subtitle.setVisibility(View.GONE);
        }

        return rowView;
    }

    public void updateEntries(ArrayList<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

}
