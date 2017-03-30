package com.awchoudhary.bookpocket;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by awaeschoudhary on 3/21/17.
 * displays details for book
 */

public class ViewDetailsTabFragment extends Fragment{
    private static final String BOOK_KEY = "book_key";

    public static ViewDetailsTabFragment newInstance(Book book) {
        ViewDetailsTabFragment fragment = new ViewDetailsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BOOK_KEY, book);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_details_tab_fragment, container, false);
        Book book = (Book) getArguments().getSerializable(BOOK_KEY); // Book to be viewed/Edited

        populateFields(view, book);

        return view;
    }

    //populate fields in the view
    private void populateFields(View view, Book b){
        ImageView cover = (ImageView) view.findViewById(R.id.coverImage);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView author = (TextView) view.findViewById(R.id.author);
        TextView numPages = (TextView) view.findViewById(R.id.numPages);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        TextView description = (TextView) view.findViewById(R.id.descriptionText);
        TextView notes = (TextView) view.findViewById(R.id.notesText);

        //load image. TODO: make cover image responsive
        Glide.with(this)
                .load(b.getCoverUrl())
                .override(257, 389)
                .error(R.drawable.default_cover_image_big)
                .into(cover);

        //set rating
        ratingBar.setRating(b.getRatings());

        //set title values
        String titleString = b.getName();
        //if there is a subtitle, append it to the title
        if(b.getSubtitle() != null && !b.getSubtitle().equals("")){
            titleString += " (" + b.getSubtitle() + ")";
        }
        title.setText(titleString);

        //populate other text fields
        numPages.setText(Integer.toString(b.getNumPages()) + " Pages");
        author.setText(b.getAuthor());
        description.setText((b.getDescription() != null && !b.getDescription().equals("")) ? b.getDescription() : "No Description");
        notes.setText((b.getNotes() != null && !b.getNotes().equals("")) ? b.getNotes() : "No Notes");
    }

}
