package com.awchoudhary.bookpocket;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

/**
 * Created by awaeschoudhary on 2/28/17.
 */

public class ViewBookActivity  extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);

        //get selected book
        Book book = (Book)getIntent().getSerializableExtra("book");

        //set title for action bar to name of selected book
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewBook);
        toolbar.setTitle(book.getName());
        setSupportActionBar(toolbar);

        //populate fields
        populateFields(book);

    }

    //populate fields in the view
    private void populateFields(Book b){
        ImageView cover = (ImageView) findViewById(R.id.coverImage);
        TextView title = (TextView) findViewById(R.id.title);
        TextView author = (TextView) findViewById(R.id.author);
        TextView numPages = (TextView) findViewById(R.id.numPages);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView description = (TextView) findViewById(R.id.descriptionText);

        //load image
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
        description.setText(b.getDescription() != null ? b.getDescription() : "No Description");

    }
}
