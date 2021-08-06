package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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

public class BooksByCategory extends AppCompatActivity {

    TextView mCategoryTitleTv;

    //get this from extras
    String mCategoryNameEN;
    String mCategoryNameAR;
    String mCategoryNameKU;

    String currentLanguage;
    String chosenCategoryLang;

    RecyclerView mBooksRecyclerView;
    List<Book> mCategoryBooksList;
    BooksCardAdapter mCategoryBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_by_category);

        //get current language and set the layout direction in this screen
        setUpLanguage();
        
        //set the title
        mCategoryTitleTv = findViewById(R.id.title_txt);
        mCategoryNameEN = getIntent().getStringExtra(Const.KEY_CATEGORIES_EN);
        mCategoryNameAR = getIntent().getStringExtra(Const.KEY_CATEGORIES_AR);
        mCategoryNameKU = getIntent().getStringExtra(Const.KEY_CATEGORIES_KU);

        //set the category name based on current app language
        switch (currentLanguage){
            case SharedPrefs.APP_LANGUAGE_ENGLISH:
                chosenCategoryLang = mCategoryNameEN;
                break;
            case SharedPrefs.APP_LANGUAGE_ARABIC:
                chosenCategoryLang = mCategoryNameAR;
                break;
            case SharedPrefs.APP_LANGUAGE_KURDISH:
                chosenCategoryLang = mCategoryNameKU;
                break;
        }

        //first use of category needing language preference
        mCategoryTitleTv.setText(chosenCategoryLang);



        mBooksRecyclerView = findViewById(R.id.books_by_category_recycler_view);



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
            mCategoryBooksList = new ArrayList<>();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.BOOKS_REFERENCE);
            Query query = ref.orderByChild(Const.BOOK_DETAILS_CATEGORY_NAME).equalTo(mCategoryNameEN);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCategoryBooksList.clear();
                    if (snapshot.getChildrenCount() == 0) {
                        progressDialog.dismiss();
                    }
                    if (snapshot != null) {
                        for (DataSnapshot booksInfo : snapshot.getChildren()) {
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            Book book = booksInfo.getValue(Book.class);
                            mCategoryBooksList.add(book);
                        }

                        mCategoryBooksAdapter = new BooksCardAdapter(BooksByCategory.this, mCategoryBooksList);
                        mBooksRecyclerView.setAdapter(mCategoryBooksAdapter);

                    } else {
                        //todo make a better way for loading visualisation
                        progressDialog.dismiss();
                        //todo make a string text
                        Toast.makeText(BooksByCategory.this, "couldn't fetch data please check your internet connection", Toast.LENGTH_SHORT).show();
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

    private void setUpLanguage() {
        currentLanguage = LocaleHelper.getLanguage(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}