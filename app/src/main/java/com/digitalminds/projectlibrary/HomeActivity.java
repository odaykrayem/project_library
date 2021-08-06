package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.paris.Paris;
import com.digitalminds.projectlibrary.adapters.CategoriesCardsAdapter;
import com.digitalminds.projectlibrary.adapters.DownloadedBooksAdapter;
import com.digitalminds.projectlibrary.adapters.RecentBooksAdapter;
import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.models.Category;
import com.digitalminds.projectlibrary.offlinedata.Booksdb;
import com.digitalminds.projectlibrary.offlinedata.Myappdatabas;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.digitalminds.projectlibrary.utils.Utility;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    //to change it's style when changing layout direction
    MaterialCardView TrendingBox;

    String currentLanguage;

    ArrayList<Category> categories;
    RecyclerView mCategoriesHomeListView;
    CategoriesCardsAdapter mCatAdapter;
    
    List<Book> recentBooks;
    RecyclerView mRecentBooksHomeListView;
    RecentBooksAdapter mRecentBooksAdapter;

    List<Booksdb> downloadedBooks;
    RecyclerView mDownloadedBooksHomeListView;
    DownloadedBooksAdapter mDownloadedBooksAdapter;

    AppCompatEditText mSearchEditText;

    String redirectMessage = "";

    ImageView settingsBtn;

    //to handle search queries
    String query = "";

    //creating room database
    public static Myappdatabas myappdatabas;

//    String fireBaseUrl = "https://firebase.google.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.home_bg_color));
        }

        //initializing room database
        myappdatabas = Room.databaseBuilder(getApplicationContext(),Myappdatabas.class,"userdb").allowMainThreadQueries().build();

        //binding
        TrendingBox = findViewById(R.id.trending_box);
        mRecentBooksHomeListView = findViewById(R.id.trending_home_list_view);
        mCategoriesHomeListView = findViewById(R.id.categories_home_list_view);
        mDownloadedBooksHomeListView = findViewById(R.id.downloads_home_list_view);
        mSearchEditText = findViewById(R.id.search_edit_text);

        //working with search edit text.
        //changing label of "enter" button into search instead
        mSearchEditText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

        mSearchEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (event==null) {
                query = mSearchEditText.getText().toString();
                if (actionId== EditorInfo.IME_ACTION_DONE)
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
        });


        //get the chosen language
        //store the current language in currentLang;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setUpLanguage();
        }

        settingsBtn = findViewById(R.id.settings_btn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });



        //categories list related
        mCategoriesHomeListView.setHasFixedSize(true);
        LinearLayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCategoriesHomeListView.setLayoutManager(catLayoutManager);

        //displaying recent books in home activity
        //progressBar
        //todo make a better way for loading visualisation
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //first check if user is connected to internet
        if (Utility.checkInternetConnection(this)) {
            //todo need to check the access for firebase in another thread cuz we can't make connection on UI thread
//            if(!Utility.isReachableUrl(getApplicationContext(), fireBaseUrl)) {
//                //todo add string resource
//                redirectMessage = "you must connect to a vpn service first because our services are not available in your country";
//                redirectToOfflineMode(redirectMessage);
//            }
            // connected to the internet continue ...
            recentBooks = new ArrayList<>();
            mRecentBooksAdapter = new RecentBooksAdapter(this, recentBooks);
            mRecentBooksHomeListView.setHasFixedSize(true);
            LinearLayoutManager recentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecentBooksHomeListView.setLayoutManager(recentLayoutManager);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.BOOKS_REFERENCE);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recentBooks.clear();
                    if(snapshot.getChildrenCount() == 0){
                        progressDialog.dismiss();
                    }
                    if(snapshot != null){
                        for (DataSnapshot booksInfo : snapshot.getChildren()){
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            Book book = booksInfo.getValue(Book.class);
                            recentBooks.add(book);
                        }
                        mRecentBooksHomeListView.setAdapter(mRecentBooksAdapter);
                    }else{
                        //todo make a better way for loading visualisation
                        progressDialog.dismiss();
                        //todo make a string text
                        Toast.makeText(HomeActivity.this, "couldn't fetch data please check your internet Access", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //connected :
            categories = new ArrayList<>();
            DatabaseReference catRef = FirebaseDatabase.getInstance().getReference(Const.CATEGORIES_REFERENCE);
            catRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    categories.clear();
                    if(snapshot != null){
                        for (DataSnapshot categoryInfo : snapshot.getChildren()){
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            Category category = categoryInfo.getValue(Category.class);
                            String name = category.getCategoryName(currentLanguage);
                            //checking if this categoryName equals all in different languages too
                            //todo CHANNNNGGGGEEESSSSSS
                            if(name.equals(Const.CATEGORY_ALL_EN.trim())||name.equals(Const.CATEGORY_ALL_AR.trim())||name.equals(Const.CATEGORY_ALL_KU.trim()))
                                continue;
                            categories.add(category);
                        }
                        mCatAdapter = new CategoriesCardsAdapter(categories, HomeActivity.this);
                        mCategoriesHomeListView.setAdapter(mCatAdapter);
                    }else{
                        //todo make a better way for loading visualisation
                        progressDialog.dismiss();
                        //todo make a string text
                        Toast.makeText(getApplicationContext(), "couldn't fetch data please check your internet Access", Toast.LENGTH_SHORT).show();
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

            //todo add string resource
            redirectMessage =  "Please check your Internet Connection";
            //redirecting to offline mode (no internet access)
            redirectToOfflineMode(redirectMessage);
        }

        downloadedBooks = myappdatabas.myDao().getBooks();
        mDownloadedBooksAdapter = new DownloadedBooksAdapter(this, downloadedBooks);
        mDownloadedBooksHomeListView.setHasFixedSize(true);
        LinearLayoutManager downloadedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mDownloadedBooksHomeListView.setLayoutManager(downloadedLayoutManager);
        mDownloadedBooksHomeListView.setAdapter(mDownloadedBooksAdapter);

    }

    private void gotoSearchResultsActivity(String query) {
        Intent i = new Intent(this, SearchQueryResults.class);
        i.putExtra(Const.KEY_SEARCH_QUERY, query);
        startActivity(i);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void setUpLanguage() {

        currentLanguage = LocaleHelper.getLanguage(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadOfflineData();
        setUpLanguage();
        mSearchEditText.setText("");
        mSearchEditText.clearFocus();
    }

    private void loadOfflineData() {
        downloadedBooks.clear();
        downloadedBooks = myappdatabas.myDao().getBooks();
        mDownloadedBooksAdapter = new DownloadedBooksAdapter(this, downloadedBooks);
        mDownloadedBooksHomeListView.setAdapter(mDownloadedBooksAdapter);

    }

    // find a way to know if we need proxy to get the data
    private void redirectToOfflineMode(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DownloadsActivity.class));
        finish();
    }

    public void goToDownloadsActivity(View View){
        startActivity(new Intent(this, DownloadsActivity.class));
    }
    public void goToBooksActivity(View View){
        startActivity(new Intent(this,BooksActivity.class));
    }


}