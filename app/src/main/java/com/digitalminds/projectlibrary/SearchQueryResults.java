package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalminds.projectlibrary.adapters.BooksCardAdapter;
import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.digitalminds.projectlibrary.utils.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryResults extends AppCompatActivity {

    String currentLang;

    TextView mSearchQueryTV;

    RecyclerView mBooksRecyclerView;
    List<Book> mBooksList;
    BooksCardAdapter mBooksAdapter;

    public String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query_results);

        setUpLanguage();

        //getting data from callers
        Intent recievedData = getIntent();
        query = recievedData.getStringExtra(Const.KEY_SEARCH_QUERY);


        mBooksRecyclerView = findViewById(R.id.books_by_search_query_recycler_view);
        mSearchQueryTV = findViewById(R.id.query_text);

        //set the query text into the header line
        mSearchQueryTV.setText(query);


        mBooksRecyclerView.setHasFixedSize(true);
        int spanCount = Utility.calculateNoOfColumnsForBooksGridDisplay(this, getResources().getDimension(R.dimen.book_width)) + 1;
        GridLayoutManager bookLayoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        mBooksRecyclerView.setLayoutManager(bookLayoutManager);

        //displaying  books based on the chosen category

        //progressBar
        //todo make a better way for loading visualisation
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //check if user is connected to internet
        if (Utility.checkInternetConnection(this)) {
            // connected to the internet continue ...
            mBooksList = new ArrayList<>();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.BOOKS_REFERENCE);
//            String searchInputToLower = query.toLowerCase();
//            String searchInputTOUpper = query.toUpperCase();
//            Query query = ref.orderByChild(Const.BOOK_DETAILS_KEY_TITLE).startAt(this.query).endAt(this.query+"\uf8ff");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mBooksList.clear();
                    if (snapshot.getChildrenCount() == 0) {
                        progressDialog.dismiss();
                    }
                    if (snapshot != null) {

                        for (DataSnapshot booksInfo : snapshot.getChildren()) {
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            Book book = booksInfo.getValue(Book.class);
                            String bookTitle = book.getBookTitle();
                            //find out if the book title contains the search query then add this book to our results
                            if(bookTitle.contains(query))
                                mBooksList.add(book);
                        }

                        mBooksAdapter = new BooksCardAdapter(SearchQueryResults.this, mBooksList);
                        mBooksRecyclerView.setAdapter(mBooksAdapter);

                    } else {
                        //todo make a better way for loading visualisation
                        progressDialog.dismiss();
                        //todo make a string text
                        Toast.makeText(SearchQueryResults.this, "couldn't fetch data please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            // not connected to the internet
            //todo make a better way for loading visualisation
            progressDialog.dismiss();
            Toast.makeText(this, "Please check your Internet Connection and try again", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


    private void setUpLanguage() {
        currentLang = LocaleHelper.getLanguage(this);
    }
}