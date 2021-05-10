package com.digitalminds.projectlibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.digitalminds.projectlibrary.adapters.CategoriesCardsAdapter;
import com.digitalminds.projectlibrary.adapters.TrendingBooksAdapter;
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.models.Category;


public class HomeActivity extends AppCompatActivity {


    Category[] categories;
    RecyclerView mCategoriesHomeListView;
    
    Book[] books;
    RecyclerView mTrendingBooksHomeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        }


        //dealing with categories we want to display on home screen
        categories = new Category[]{
                new Category("Romance", R.drawable.ic_cat_romance),
                new Category("Entertainment", R.drawable.ic_cat_entertainment),
                new Category("Religion", R.drawable.ic_cat_religion),
                new Category("Arts", R.drawable.ic_cat_arts_n_music),
                new Category("Sports", R.drawable.ic_cat_sports),
                new Category("Science", R.drawable.ic_cat_science_n_math)
        };

        mCategoriesHomeListView = findViewById(R.id.categories_home_list_view);
        CategoriesCardsAdapter catAdapter = new CategoriesCardsAdapter(categories);
        mCategoriesHomeListView.setHasFixedSize(true);
        LinearLayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCategoriesHomeListView.setLayoutManager(catLayoutManager);
        mCategoriesHomeListView.setAdapter(catAdapter);
        
        //displaying trending books in home activity
        books = new Book[]{
                new Book("title 1", "saad haj"),
                new Book("title 2", "mohammad abd"),
                new Book("title 3", "khaled othman"),
                new Book("title 4", "nana alhaj"),
                new Book("title 5", "abdulla dekmak"),
                new Book("and title 6", "ali somati")
        };

        mTrendingBooksHomeListView = findViewById(R.id.trending_home_list_view);
        TrendingBooksAdapter trendingBooksAdapter = new TrendingBooksAdapter(books);
        mTrendingBooksHomeListView.setHasFixedSize(true);
        LinearLayoutManager trendingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrendingBooksHomeListView.setLayoutManager(trendingLayoutManager);
        mTrendingBooksHomeListView.setAdapter(trendingBooksAdapter);

    }
}