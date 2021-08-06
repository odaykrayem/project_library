package com.digitalminds.projectlibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.digitalminds.projectlibrary.adapters.DownloadedBooksAdapter;
import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.offlinedata.Booksdb;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.digitalminds.projectlibrary.utils.Utility;

import java.util.List;

import static com.digitalminds.projectlibrary.HomeActivity.myappdatabas;

public class DownloadsActivity extends AppCompatActivity {

    List<Booksdb> downloadedBooks;
    RecyclerView mDownloadedBooksHomeListView;
    DownloadedBooksAdapter mDownloadedBooksAdapter;

    String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        setUpLanguage();

        mDownloadedBooksHomeListView = findViewById(R.id.downloads_book_recycler_view);

        downloadedBooks = myappdatabas.myDao().getBooks();
        mDownloadedBooksAdapter = new DownloadedBooksAdapter(this, downloadedBooks);
        mDownloadedBooksHomeListView.setHasFixedSize(true);
        int spanCount = Utility.calculateNoOfColumnsForBooksGridDisplay(this, getResources().getDimension(R.dimen.book_width)) + 1;
        GridLayoutManager downloadedLayoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        mDownloadedBooksHomeListView.setLayoutManager(downloadedLayoutManager);
        mDownloadedBooksHomeListView.setAdapter(mDownloadedBooksAdapter);

    }

    private void setUpLanguage() {

        currentLanguage = LocaleHelper.getLanguage(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}