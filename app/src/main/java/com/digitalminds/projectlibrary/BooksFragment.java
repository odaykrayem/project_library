package com.digitalminds.projectlibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {

    RecyclerView mBooksRecyclerView;
    BooksCardAdapter mBooksAdapter;
    List<Book> books;

    AppCompatEditText mSearchEditText;
    
    String currentLanguage;
    String chosenCategoryLang;

    //to handle search queries
    String query = "";

    // for different language approaches
    private String mCategoryEN;
    private String mCategoryAR;
    private String mCategoryKU;

    public BooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param categoryAR Parameter 1
     * @param categoryEN Parameter 2
     * @param categoryKU Parameter 3
     * @return A new instance of fragment BooksFragment.
     */
    public static BooksFragment newInstance(String categoryEN, String categoryAR, String categoryKU) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putString(Const.KEY_CATEGORIES_EN, categoryEN);
        args.putString(Const.KEY_CATEGORIES_AR, categoryAR);
        args.putString(Const.KEY_CATEGORIES_KU, categoryKU);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategoryEN = getArguments().getString(Const.KEY_CATEGORIES_EN);
            mCategoryAR = getArguments().getString(Const.KEY_CATEGORIES_AR);
            mCategoryKU = getArguments().getString(Const.KEY_CATEGORIES_KU);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize view parent
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        //binding
        mBooksRecyclerView = view.findViewById(R.id.frag_book_recycler_view);
        mBooksRecyclerView.setHasFixedSize(true);
        mSearchEditText = view.findViewById(R.id.search_edit_text);

        //working with search edit text.
        //changing label of "enter" button into search instead
        mSearchEditText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (event==null) {
                    query = mSearchEditText.getText().toString();
                    if (actionId==EditorInfo.IME_ACTION_DONE)
                        // Capture soft enters in a singleLine EditText that is the last EditText.
                        gotoSearchResultsActivity(query);
                    else if (actionId==EditorInfo.IME_ACTION_NEXT)
                        // Capture soft enters in other singleLine EditTexts
                        gotoSearchResultsActivity(query);
                    else return false;  // Let system handle all other null KeyEvents
                }

                else
                    return false;

                return false;
            }
        });

        //get the current app language
        currentLanguage = LocaleHelper.getLanguage(getContext());

        //choose the suitable category based on current language
        switch (currentLanguage){
            case SharedPrefs.APP_LANGUAGE_ENGLISH:
                chosenCategoryLang = mCategoryEN;
                break;
            case SharedPrefs.APP_LANGUAGE_ARABIC:
                chosenCategoryLang = mCategoryAR;
                break;
            case SharedPrefs.APP_LANGUAGE_KURDISH:
                chosenCategoryLang = mCategoryKU;
                break;
        }

        //now displaying  books based on the chosen category
        //progressBar
        //todo make a better way for loading visualisation
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //check if user is connected to internet
        if (Utility.checkInternetConnection(getContext())) {
            // connected to the internet continue ...
            books = new ArrayList<>();
            mBooksAdapter = new BooksCardAdapter(getContext(), books);
            int spanCount = Utility.calculateNoOfColumnsForBooksGridDisplay(getContext(), getResources().getDimension(R.dimen.book_width)) + 1;
            GridLayoutManager gridBooksManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false);
            mBooksRecyclerView.setLayoutManager(gridBooksManager);

            if(mCategoryEN.equals(Const.CATEGORY_ALL_EN)||mCategoryAR.equals(Const.CATEGORY_ALL_AR)||mCategoryKU.equals(Const.CATEGORY_ALL_KU)) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.BOOKS_REFERENCE);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        books.clear();
                        if(snapshot.getChildrenCount() == 0){
                            progressDialog.dismiss();
                        }
                        if(snapshot != null){
                            for (DataSnapshot booksInfo : snapshot.getChildren()){
                                //todo make a better way for loading visualisation
                                progressDialog.dismiss();
                                Book book = booksInfo.getValue(Book.class);
                                books.add(book);
                            }
                            mBooksRecyclerView.setAdapter(mBooksAdapter);
                        }else{
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            //todo make a string text
                            Toast.makeText(getContext(), "couldn't fetch data please check your internet Access", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.BOOKS_REFERENCE);
                Query query = ref.orderByChild(Const.BOOK_DETAILS_CATEGORY_NAME_EN).equalTo(mCategoryEN);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        books.clear();
                        if (snapshot.getChildrenCount() == 0) {
                            progressDialog.dismiss();
                        }
                        if (snapshot != null) {
                            for (DataSnapshot booksInfo : snapshot.getChildren()) {
                                //todo make a better way for loading visualisation
                                progressDialog.dismiss();
                                Book book = booksInfo.getValue(Book.class);
                                books.add(book);
                            }
                            mBooksRecyclerView.setAdapter(mBooksAdapter);
                        } else {
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            //todo make a string text
                            Toast.makeText(getContext(), "couldn't fetch data please check your internet Access", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            // not connected to the internet
            //todo make a better way for loading visualisation
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getContext(), DownloadsActivity.class);
            startActivity(i);
            getActivity().finish();
        }


        return view;
    }

    private void gotoSearchResultsActivity(String query) {
        Intent i = new Intent(getContext(), SearchQueryResults.class);
        i.putExtra(Const.KEY_SEARCH_QUERY, query);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchEditText.setText("");
        mSearchEditText.clearFocus();
    }





}