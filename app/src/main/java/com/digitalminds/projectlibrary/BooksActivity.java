package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digitalminds.projectlibrary.adapters.BooksViewPagerAdapter;
import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.models.Category;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.digitalminds.projectlibrary.utils.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity {

    //initializing variables
    DachshundTabLayout mCategoriesTabLayout;
    BooksViewPagerAdapter mTabsAdapter;
    List<Category> categories;
    ViewPager mBooksPager;

    ImageView backBtn;

    String currentLanguage;



    private static final String books_fragment_category_key = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for changing status bar icon colors
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.home_bg_color));

        setContentView(R.layout.activity_books);

        //get the current using language
        currentLanguage = SharedPrefs.getString(this, SharedPrefs.GENERAL_FILE, SharedPrefs.KEY_APP_LANGUAGE_ID, SharedPrefs.APP_LANGUAGE_ENGLISH);

        //binding
        mCategoriesTabLayout = findViewById(R.id.categories_tab_layout);
        mBooksPager = findViewById(R.id.books_pager);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());


        //getting data from firebase and display category names on tabs
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //check internet connection
        if(Utility.checkInternetConnection(this))
        {
            //connected :
            categories = new ArrayList<>();

            //todo CHANNNNNNGGGGGGEEEEESSSS
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Const.CATEGORIES_REFERENCE);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    categories.clear();
                    if(snapshot != null){
                        for (DataSnapshot categoryInfo : snapshot.getChildren()){
                            //todo make a better way for loading visualisation
                            progressDialog.dismiss();
                            Category category = categoryInfo.getValue(Category.class);
                            categories.add(category);
                        }
                        prepareBookPager(mBooksPager, categories);
                        mCategoriesTabLayout.setupWithViewPager(mBooksPager);
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

        }else{
            //disconnected :
            //todo make a better way for loading visualisation
            progressDialog.dismiss();
            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            //todo : after making the downloaded books activity redirect the user into downloaded books for offline mode 0_~

        }




    }

    //todo make sure this new code works
    private void prepareBookPager(ViewPager mBooksPager, List<Category> categories) {
        //initialize the adapter
        mTabsAdapter = new BooksViewPagerAdapter(getSupportFragmentManager());
        //for loop to make new instances from this fragment based on the real data
        for(int i = 0; i<categories.size(); i++){
            Category category = categories.get(i);
            mTabsAdapter.addFragment(BooksFragment.newInstance(category.getCategoryNameEN(), category.getCategoryNameAR(), category.getCategoryNameKU()), category.getCategoryName(currentLanguage));
        }
        mBooksPager.setAdapter(mTabsAdapter);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}