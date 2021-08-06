package com.digitalminds.projectlibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;

import java.util.List;

public class RecentBooksAdapter extends RecyclerView.Adapter<RecentBooksAdapter.ViewHolder>{


    private List<Book> books;
    String currentLang;
    Context context;

    // RecyclerView recyclerView;
    public RecentBooksAdapter(Context context, List<Book> books) {
        this.books = books;
        this.context = context;
        currentLang = SharedPrefs.getString(context, SharedPrefs.GENERAL_FILE, SharedPrefs.KEY_APP_LANGUAGE_ID, SharedPrefs.APP_LANGUAGE_ENGLISH);
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
        final Book book = books.get(position);

        //todo : update the image measurements if needed
        //set the book image
        Glide.with(context)
                .load(book.getBookImagePath())
                .into(holder.bookImage);

        holder.bookTitle.setText(book.getBookTitle());
        holder.bookAuthorName.setText(book.getBookAuthorName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BookDetailsActivity.class);
                i.putExtra(Const.BOOK_DETAILS_KEY_IMAGE, book.getBookImagePath());
                i.putExtra(Const.BOOK_DETAILS_KEY_TITLE, book.getBookTitle());
                i.putExtra(Const.BOOK_DETAILS_KEY_AUTHOR, book.getBookAuthorName());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_EN, book.getBookCategoryNameEN());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_AR, book.getBookCategoryNameAR());
                i.putExtra(Const.BOOK_DETAILS_KEY_CATEGORY_KU, book.getBookCategoryNameKU());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_EN, book.getBookDescriptionEN());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_AR, book.getBookDescriptionAR());
                i.putExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_KURD, book.getBookDescriptionKU());
                i.putExtra(Const.BOOK_DETAILS_KEY_URL, book.getBookPdfUrl());
                i.putExtra(Const.BOOK_DETAILS_KEY_ID, book.getBookId());
                i.putExtra(Const.BOOK_DETAILS_KEY_DOWNLOADS, book.getBookDownloads());
                i.putExtra(Const.BOOK_DETAILS_KEY_PAGES, book.getBookPagesNumber());
                i.putExtra(Const.BOOK_DETAILS_KEY_LANGUAGE, book.getBookLanguage());
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
        public TextView bookAuthorName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.bookImage = itemView.findViewById(R.id.trending_book_image);
            this.bookTitle = itemView.findViewById(R.id.trending_book_title);
            this.bookAuthorName = itemView.findViewById(R.id.trending_book_author_name);
        }
    }
}

