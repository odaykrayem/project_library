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

public class TrendingBooksAdapter extends RecyclerView.Adapter<TrendingBooksAdapter.ViewHolder>{


    private Book[] books;

    // RecyclerView recyclerView;
    public TrendingBooksAdapter(Book[] books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the usual shit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.trending_book_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books[position];
        holder.bookImage.setImageResource(R.drawable.home_header_bg);
        holder.bookTitle.setText(book.getBookTitle());
        holder.bookAuthorName.setText(book.getBookAuthorName());


    }

    @Override
    public int getItemCount() {
        return books.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public TextView bookTitle;
        public TextView bookAuthorName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.bookImage = itemView.findViewById(R.id.trending_book_image);
            this.bookTitle = itemView.findViewById(R.id.trending_book_title);
            this.bookAuthorName = itemView.findViewById(R.id.trending_book_author_name);
        }
    }
}

