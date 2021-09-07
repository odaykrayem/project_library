package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.offlinedata.Booksdb;
import com.digitalminds.projectlibrary.utils.Const;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;

public class BookDetailsActivity extends AppCompatActivity {
    private static final int WRITE_EXT_STORAGECODE = 1;
    private static final int READ_EXT_STORAGECODE = 2;

    String currentLanguage;

    ImageView downloadBtn;
    Button read;
    ImageView bookImageBack;
    TextView bookNameBack;
    TextView authorNameBack;
    TextView bookCategory1;
    TextView downloads;
    TextView descriptionText;

    ImageView backBtn;

    //extras
    TextView bookLanguage;
    TextView bookPagesNumber;

    AdView mAdView;
    private InterstitialAd mInterstitialAd = null;

    // used for saving book image to gallery
    Bitmap bitmap;
    File file;

    ProgressDialog progressDialog;


    //from intent ... came from server
    String bookName, authorName, bookImage, pdfUrl, descriptionEN, descriptionAR, descriptionKURD, downloadsstring, bookCategoryEN, bookCategoryAR, bookCategoryKU, bookPagesNumberString, bookLanguageString = "";
    String id = "";


    //to know if the book is downloaded or not
    boolean is_downloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        setUpLanguage();

        progressDialog = new ProgressDialog(this);
        ((ProgressDialog) progressDialog).setMessage("Please wait\n Downloading pdf...");
        progressDialog.setCancelable(false);

        //getting data from callers
        Intent recievedData = getIntent();
        bookName = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_TITLE);
        authorName = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_AUTHOR);
        bookImage = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_IMAGE);
        pdfUrl = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_URL);
        descriptionEN = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_EN);
        descriptionAR = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_AR);
        descriptionKURD = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_DESCRIPTION_KURD);
        bookCategoryEN = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_CATEGORY_EN);
        bookCategoryAR = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_CATEGORY_AR);
        bookCategoryKU = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_CATEGORY_KU);
        downloadsstring = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_DOWNLOADS);
        id = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_ID);
        bookPagesNumberString = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_PAGES);
        bookLanguageString = recievedData.getStringExtra(Const.BOOK_DETAILS_KEY_LANGUAGE);

        //binding
        downloadBtn = findViewById(R.id.book_download_btn);
        bookImageBack = findViewById(R.id.book_details_image);
        bookCategory1 = findViewById(R.id.book_details_category_txt);
        authorNameBack = findViewById(R.id.book_details_author_name_txt);
        bookNameBack = findViewById(R.id.book_details_title);
        downloads = findViewById(R.id.book_details_downloads_txt);
        descriptionText = findViewById(R.id.book_details_description_txt);
        read = findViewById(R.id.read_book);
        bookLanguage = findViewById(R.id.book_details_language_txt);
        bookPagesNumber = findViewById(R.id.book_details_pages_txt);

        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(v -> onBackPressed());

        //setting selected book data
        Glide.with(this)
                .load(bookImage)
                .into(bookImageBack);

        bookNameBack.setText(bookName);
        authorNameBack.setText(authorName);

        //set the description and category based on current app language
        switch (currentLanguage){
            case SharedPrefs.APP_LANGUAGE_ENGLISH:
                descriptionText.setText(descriptionEN);
                bookCategory1.setText(bookCategoryEN);
                break;
            case SharedPrefs.APP_LANGUAGE_ARABIC:
                descriptionText.setText(descriptionAR);
                bookCategory1.setText(bookCategoryAR);
                break;
            case SharedPrefs.APP_LANGUAGE_KURDISH:
                descriptionText.setText(descriptionKURD);
                bookCategory1.setText(bookCategoryKU);
                break;
        }

        bookPagesNumber.setText(bookPagesNumberString);
        try {
            downloads.setText(downloadsstring);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check if the book is downloaded
        if(isDownloaded()){
            downloadBtn.setClickable(false);
            downloadBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_on_my_device, null));
        }else{
            downloadBtn.setClickable(true);
            downloadBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_download, null));
        }


        //initialize for ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //bind view and load banner ads for it
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //set listener for adView to make some actions
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        AdRequest interstatialAdRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        //handle interstatialAd events
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed
                BookDetailsActivity.this.mInterstitialAd = null;
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                BookDetailsActivity.this.mInterstitialAd = null;
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;

                Log.d("TAG", "The ad was shown.");
            }
        });



        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storing value in array list
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        String[] permission = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        };
                        requestPermissions(permission, WRITE_EXT_STORAGECODE);
                        return;
                    }
                }

//                if(mInterstitialAd != null){
//                    mInterstitialAd.show(BookDetailsActivity.this);
//                }else{
                    //the interstatialAd wasn't loaded successfuly
                    downloadBook();
//                }


            }

            //downloadBook
            public void downloadBook(){
                // for saving the book image
                bitmap = ((BitmapDrawable) bookImageBack.getDrawable()).getBitmap();

                //start loading animations
                progressDialog.show();

                // start by downloading the pdf file into internal storage
                savepdf();
            }

            public void savepdf() {

                FileLoader.with(getApplicationContext())
                        .load(pdfUrl, false) //2nd parameter is optioal, pass true to force load from network
                        .fromDirectory("test4", FileLoader.DIR_INTERNAL)
                        .asFile(new FileRequestListener<File>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {

                                //after downloading pdf now we are going to keep our
                                //book img in storage and save other book parameters
                                // and finally increment the book's number of downloads
                                saveBookDetailsAndImage();
                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {
                                Toast.makeText(BookDetailsActivity.this, "error while downloading pdf", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        });


        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //todo load an ad here after the developing process is done
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                        return;
//                    } else {
//                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                    }
//                }else{
                    //todo implement the readers number section if ordered
                    //this snippet is for incrementing the readers number so if you
                    //still interested in showing readers number just uncomment it and make it work for your configs
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
//                    final Query query = reference.orderByChild("id").equalTo(id);
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            if (snapshot != null) {
//                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                                    Book books = snapshot1.getValue(Book.class);
//                                    if (snapshot1.child("readers").exists()) {
//                                        String readers = books.getReaders();
//                                        int integerReader = (Integer.parseInt(readers)) + 1;
//                                        books.setReaders(String.valueOf(integerReader));
//                                        reference.child(id).setValue(books);
//                                        readersText.setText(books.getReaders());
//                                    } else {
//                                        books.setReaders("1");
//                                        reference.child(id).setValue(books);
//                                        readersText.setText(books.getReaders());
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

                    Intent i = new Intent(BookDetailsActivity.this, BookView.class);
                    i.putExtra(Const.BOOK_DETAILS_KEY_URL, pdfUrl);
                    i.putExtra(Const.BOOK_DETAILS_KEY_ID, id);
                    i.putExtra(Const.BOOK_DETAILS_KEY_TITLE, bookName);
                    i.putExtra(Const.BOOK_DETAILS_KEY_PAGES, bookPagesNumberString);
                    startActivity(i);
//                }


            }
        });
    }

    private boolean isDownloaded() {
        Booksdb book = HomeActivity.myappdatabas.myDao().getBook(id);
        if( book != null){
            return true;
        }else
            return false;
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        //check if the book is downloaded
        if(isDownloaded()){
            downloadBtn.setClickable(false);
            downloadBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_on_my_device, null));
        }else{
            downloadBtn.setClickable(true);
            downloadBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_download, null));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveBookDetailsAndImage() {

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/.eBookImages");
        dir.mkdir();
        String imagename = time + ".JPEG";
        file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();


            //now changing the shape of the download button
            downloadBtn.setClickable(false);
            downloadBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_on_my_device, null));

            // incrementing book downloads counter
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books").child(id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String dwnlds1 = snapshot.child("bookDownloads").getValue(String.class);

                    Log.e("TAGFIRE", "onDataChange: " + snapshot + ":" + dwnlds1 + "id" + id);

                    int val = Integer.parseInt(dwnlds1);
                    val = val + 1;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("bookDownloads", String.valueOf(val));
                    final int finalVal = val;
                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            downloads.setText("" + finalVal);
                            Toast.makeText(BookDetailsActivity.this, "Download complete ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Booksdb bk = new Booksdb();
            bk.setBookName(bookName);
            bk.setDownloads(downloadsstring);
            bk.setCategoryNameEn(bookCategoryEN);
            bk.setCategoryNameAr(bookCategoryAR);
            bk.setCategoryNameKu(bookCategoryKU);
            bk.setAuthorName(authorName);
            bk.setBookImage(String.valueOf(file));
            bk.setPdfUrl(pdfUrl);
            bk.setDescriptionEN(descriptionEN);
            bk.setDescriptionAR(descriptionAR);
            bk.setDescriptionKURD(descriptionKURD);
            bk.setId(id);
            bk.setLanguage(bookLanguageString);
            bk.setPagesNumber(bookPagesNumberString);

            //saving book details offline
            HomeActivity.myappdatabas.myDao().addBook(bk);
            progressDialog.dismiss();


            // Toast.makeText(PdfDetail.this, "Saved in DCIM", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    String[] permission = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    };
                    requestPermissions(permission, WRITE_EXT_STORAGECODE);
                }
            }
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