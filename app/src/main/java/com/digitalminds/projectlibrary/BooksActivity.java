package com.digitalminds.projectlibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.digitalminds.projectlibrary.R;
import com.digitalminds.projectlibrary.adapters.BooksViewPagerAdapter;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BooksActivity extends AppCompatActivity {

    //initializing variables
    DachshundTabLayout mCategoriesTabLayout;
    ViewPager mBooksPager;

    private static final String books_fragment_category_key = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.home_bg_color));
        }
        setContentView(R.layout.activity_books);

        //linking
        mCategoriesTabLayout = findViewById(R.id.categories_tab_layout);
        mBooksPager = findViewById(R.id.books_pager);

        //we've got to get the values for this list from server
        ArrayList<String> tabsTitles = new ArrayList<>();

        //insert dummy data
        //todo get the values from server
        tabsTitles.add("All");
        tabsTitles.add("Romance");
        tabsTitles.add("fantasy");
        tabsTitles.add("mystry");
        tabsTitles.add("design");
        tabsTitles.add("story");

        //prepare view pager
        prepareBookPager(mBooksPager, tabsTitles);

        //setup pager with tab layout
        mCategoriesTabLayout.setupWithViewPager(mBooksPager);


    }

    private void prepareBookPager(ViewPager mBooksPager, ArrayList<String> tabsTitles) {
        //initialize the adapter
        BooksViewPagerAdapter booksPagerAdapter = new BooksViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //initialize main fragment
        BooksFragment booksFragment = new BooksFragment();
        //for loop to make new instances from this fragment based on the real data
        for(int i = 0; i<tabsTitles.size(); i++){
            Bundle bundle = new Bundle();
            bundle.putString(books_fragment_category_key, tabsTitles.get(i));
            booksFragment.setArguments(bundle);
            booksPagerAdapter.addFragment(booksFragment, tabsTitles.get(i));
            booksFragment = new BooksFragment();
        }
        mBooksPager.setAdapter(booksPagerAdapter);

    }
}