package com.awchoudhary.bookpocket.ui.mybooksscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.awchoudhary.bookpocket.R;
import com.awchoudhary.bookpocket.ui.viewbookscreen.TabManagerActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by awaeschoudhary on 5/1/17.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    private Context context;
    private ArrayList<Book> books;

    public BooksAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        BookViewHolder bvh = new BookViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(final BookViewHolder bookViewHolder, final int position) {
        Book book = books.get(position);

        //download image into imageview.
        Glide.with(context)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.default_cover_image)
                .error(R.drawable.default_cover_image)
                .override(131, 200) //TODO: Needs to be responsive
                .into(bookViewHolder.coverImageView);

        //set values
        bookViewHolder.titleTextView.setText(book.getName());
        bookViewHolder.authorTextView.setText(book.getAuthor());

        //set cardview properties
        bookViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        bookViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            Book book = books.get(bookViewHolder.getAdapterPosition());
            @Override
            public void onClick(View v) {
                //navigate to book view screen
                Intent intent = new Intent(context, TabManagerActivity.class);
                intent.putExtra("book", book);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateEntries(Book book) {
        books.add(book);
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView authorTextView;
        ImageView coverImageView;

        BookViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_book);
            titleTextView = (TextView)itemView.findViewById(R.id.title);
            authorTextView = (TextView)itemView.findViewById(R.id.author);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImage);
        }
    }


}