package com.digitalminds.projectlibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digitalminds.projectlibrary.BookDetailsActivity;
import com.digitalminds.projectlibrary.R;
import com.digitalminds.projectlibrary.offlinedata.Booksdb;
import com.digitalminds.projectlibrary.utils.Const;

import java.util.List;

public class DownloadedBooksAdapter extends RecyclerView.Adapter<DownloadedBooksAdapter.ViewHolder>{


    private List<Booksdb> books;
    Context context;

    // RecyclerView recyclerView;
    public DownloadedBooksAdapter(Context context, List<Booksdb> books) {
        this.books = books;
        this.context = context;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Booksdb book = books.get(position);

        //todo : update the image measurements if needed
        //set the book image
        Glide.with(context)
                .load(book.getBookImage())
                .into(holder.bookImage);

        holder.bookTitle.setText(book.getBookName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BookDetailsActivity.class);
                i.putExtra(Const.BOOK_DETAILS_KEY_IMAGE, book.getBookImage());
                i.putExtra(Const.BOOK_DETAILS_KEY_TITLE, book.getBookName());
                i.putExtra(Const.BOOK_DETAILS_KEY_AUTHOR, book.getAuthorName());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_EN, book.getCategoryNameEn());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_AR, book.getCategoryNameAr());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_KU, book.getCategoryNameKu());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_EN, book.getDescriptionEN());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_AR, book.getDescriptionAR());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_KURD, book.getDescriptionKURD());
                i.putExtra(Const.BOOK_DETAILS_KEY_URL, book.getPdfUrl());
                i.putExtra(Const.BOOK_DETAILS_KEY_ID, book.getId());
                i.putExtra(Const.BOOK_DETAILS_KEY_DOWNLOADS, book.getDownloads());
                i.putExtra(Const.BOOK_DETAILS_KEY_PAGES, book.getPagesNumber());
                i.putExtra(Const.BOOK_DETAILS_KEY_LANGUAGE, book.getLanguage());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
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