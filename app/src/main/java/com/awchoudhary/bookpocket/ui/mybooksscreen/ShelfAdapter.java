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

public class ShelfAdapter extends RecyclerView.Adapter<ShelfAdapter.BookViewHolder> {
    private Context context;
    private ArrayList<Book> books;
    //Keeps track of books within RecyclerView as books array can be filtered on search
    private ArrayList<Book> booksCopy;

    public ShelfAdapter(Context context, ArrayList<Book> books, ArrayList<Book> booksCopy){
        this.context = context;
        this.books = books;
        this.booksCopy = booksCopy;
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
                //.override(131, 200) //TODO: Needs to be responsive
                .into(bookViewHolder.coverImageView);

        //set values
        bookViewHolder.titleTextView.setText(book.getName());
        bookViewHolder.authorTextView.setText(book.getAuthor());

        if(book.getDateCompleted() != null && !book.getDateCompleted().isEmpty()){
            bookViewHolder.dateTextView.setText("Completed " + book.getDateCompleted());
        }
        else if(book.getDateStarted() != null && !book.getDateStarted().isEmpty()){
            bookViewHolder.dateTextView.setText("Started " + book.getDateStarted());
        }

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
        booksCopy.add(book);
        notifyDataSetChanged();
    }

    public void clearEntries(){
        books.clear();
    }

    public void filter(String text) {
        books.clear();

        if(text.isEmpty()){
            books.addAll(booksCopy);
        }
        else{
            text = text.toLowerCase();
            for(Book book: booksCopy){
                if(book.getName().toLowerCase().contains(text)){
                    books.add(book);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;
        ImageView coverImageView;

        BookViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_book);
            titleTextView = (TextView)itemView.findViewById(R.id.card_book_title);
            authorTextView = (TextView)itemView.findViewById(R.id.card_book_author);
            dateTextView = (TextView)itemView.findViewById(R.id.card_book_date);
            coverImageView = (ImageView)itemView.findViewById(R.id.card_book_coverImage);
        }
    }
}
