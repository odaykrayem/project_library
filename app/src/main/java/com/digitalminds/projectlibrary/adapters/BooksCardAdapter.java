package com.digitalminds.projectlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalminds.projectlibrary.R;
import com.digitalminds.projectlibrary.models.Book;

public class BooksCardAdapter extends RecyclerView.Adapter<BooksCardAdapter.ViewHolder>{


    private Book[] books;

    // RecyclerView recyclerView;
    public BooksCardAdapter(Book[] books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the usual shit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.books_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull BooksCardAdapter.ViewHolder holder, int position) {
        Book book = books[position];

        //todo : use glide for static image measurements
        holder.bookImage.setImageResource(R.drawable.home_header_bg);
        holder.bookTitle.setText(book.getBookTitle());


    }

    @Override
    public int getItemCount() {
        return books.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public TextView bookTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.bookImage = itemView.findViewById(R.id.book_image);
            this.bookTitle = itemView.findViewById(R.id.book_title);
        }
    }
}

