package com.digitalminds.projectlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.models.Book;
import com.digitalminds.projectlibrary.utils.SharedPrefs;
import com.digitalminds.projectlibrary.utils.Utility;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class BookView extends AppCompatActivity implements OnLoadCompleteListener, OnPageChangeListener, OnPageErrorListener {

    //for writing on storage permission
    private static final int WRITE_EXT_STORAGE_CODE = 1;

    String currentLanguage;
    String defaultLanguage;
    
    //for binding
    PDFView pdfView;
    String id;
    BottomNavigationView bottomNav;

    //while pdf is loaded
    ProgressDialog progressDialog;

    //for getting from extras
    String url;
    String totalPage;
    String bookName;

    //playing sound for capturing image
    MediaPlayer mp;

    //values to get from shared preferences
    // for bookmarks purposes
    int pageNumber = 0;
    int bookMarkedPage = 0;
    boolean hasBookMark = false;

    // related to theme preferences
    boolean nightMode;

    //pdf file after downloading
    File pdfUrl;

    //controls showing and hiding bottom menu
    OnTapListener onTapListener;
    private boolean tapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        
        setUpLanguage();

        bottomNav = findViewById(R.id.bottom_navigation_bookView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Intent i = getIntent();
        url = i.getStringExtra("pdfUrl");
        id = i.getStringExtra("bookId");
        bookName = i.getStringExtra("bookName");


        SharedPreferences sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
        nightMode = SharedPrefs.getBoolean(this, SharedPrefs.GENERAL_FILE, SharedPrefs.KEY_NIGHT_MODE, false);
        bookMarkedPage = SharedPrefs.getInt(this, SharedPrefs.SHARED_BOOK_MARKS_FILE, SharedPrefs.KEY_BOOK_MARKED_PAGE_NUMBER + id, -1);
        hasBookMark = SharedPrefs.getBoolean(this, SharedPrefs.SHARED_BOOK_MARKS_FILE, SharedPrefs.KEY_HAS_BOOK_MARK + id, false);


        //if the pdf is large or not downloaded so it will take time to load
        pdfView = findViewById(R.id.pdfView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait\nFetching pdf...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        //showing and hiding bottom tools menu when tapping on screen
        onTapListener = new OnTapListener() {
            @Override
            public boolean onTap(MotionEvent e) {
                if (tapped) {
                    bottomNav.setVisibility(View.INVISIBLE);
                    tapped = false;
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    tapped = true;
                }
                return true;
            }
        };


        FileLoader.with(this)
                .load(url, false) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory("test4", FileLoader.DIR_INTERNAL)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfUrl = response.getBody();
                        // do something with the file
                        try {
                            pdfView.fromFile(pdfUrl)
                                    .onTap(onTapListener)
                                    .onPageScroll(new OnPageScrollListener() {
                                        @Override
                                        public void onPageScrolled(int page, float positionOffset) {
                                            bottomNav.setVisibility(View.VISIBLE);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bottomNav.setVisibility(View.INVISIBLE);
                                                }
                                            }, 1000);
                                        }
                                    })
                                    .defaultPage(0)
                                    .enableSwipe(true)
                                    .enableAnnotationRendering(true)
                                    .onLoad(BookView.this)
                                    .onPageChange(BookView.this)
                                    .scrollHandle(new DefaultScrollHandle(BookView.this))
                                    .enableDoubletap(true)
                                    .onPageError(BookView.this)
                                    .swipeHorizontal(true)
                                    .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                                    .fitEachPage(true)
                                    .pageSnap(true)
                                    .pageFling(true)
                                    .spacing(0)
                                    .nightMode(nightMode)
                                    .load();

                            Log.i("FilePath: ", "onLoad: " + pdfUrl);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(BookView.this, "" + t.getMessage() + ", File Error", Toast.LENGTH_SHORT).show();

                    }
                });



        Menu menu = bottomNav.getMenu();
        if (nightMode != true) {
            menu.findItem(R.id.nav_night).setIcon(R.drawable.ic_night);
        } else {
            menu.findItem(R.id.nav_night).setIcon(R.drawable.ic_night_filled);

        }

    }

    private void setUpLanguage() {
        currentLanguage = LocaleHelper.getLanguage(this);
    }




//todo implement ads functionality if ordered
//
//    private void loadBannerAd() {
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//    }

    @Override
    public void loadComplete(int nbPages) {
        progressDialog.dismiss();
        Log.d("Tag2", "Pages2:" + pdfView.getPageCount());

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;

        totalPage = String.valueOf(pageCount);
        bookMarkedPage = SharedPrefs.getInt(this, SharedPrefs.SHARED_BOOK_MARKS_FILE, SharedPrefs.KEY_BOOK_MARKED_PAGE_NUMBER + id, -1);


        int result = bookMarkedPage - 1;

        Menu menu = bottomNav.getMenu();
        if (result == pageNumber) {
            menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_filled);
        } else {
            menu.findItem(R.id.bookmark).setIcon(R.drawable.ic_bookmark_outline);
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void screenShot() {
        Bitmap b = Bitmap.createBitmap(pdfView.getWidth(), pdfView.getHeight(), Bitmap.Config.ARGB_8888);

        int width, height;
        width = pdfView.getWidth();
        height = pdfView.getHeight();
        Bitmap cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(cs);
        c.drawBitmap(b, 0, 0, null);
        pdfView.draw(c);
        c.setBitmap(cs);


        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/DCIM/PDFReader");
        dir.mkdir();
        String imagename = time + ".JPEG";
        File file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            cs.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            mp = MediaPlayer.create(this, R.raw.camerashutter);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                }
            });
            //for gallery to be notified of the Image
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
            //todo add string resource for locals
            Toast.makeText(BookView.this, "screenshot saved to gallery", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    String[] permission = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    requestPermissions(permission, WRITE_EXT_STORAGE_CODE);
                }
            }
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            int navId = item.getItemId();

            switch (navId) {

                case R.id.nav_night:
                    SharedPreferences sharedPreferences = getSharedPreferences("bookmarks", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (nightMode != true) {
                        editor.putBoolean("nightMode", true);
                        editor.commit();
                        item.setIcon(R.drawable.ic_night);
                        recreate();

                        Toast.makeText(BookView.this, "Night Mode turned on", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putBoolean("nightMode", false);
                        editor.commit();
                        item.setIcon(R.drawable.ic_night_filled);

                        recreate();
                        Toast.makeText(BookView.this, "Night Mode turned off", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.screen_shot:
                    screenShot();
                    break;

                case R.id.bookmark:
                    //checking wich icon to use
                    if(bookMarkedPage == (pageNumber + 1) ){
                        SharedPrefs.save(BookView.this, SharedPrefs.SHARED_BOOK_MARKS_FILE, SharedPrefs.KEY_BOOK_MARKED_PAGE_NUMBER + id, -1);
                        item.setIcon(R.drawable.ic_bookmark_outline);
                        Toast.makeText(BookView.this, "Unmarked bookmark", Toast.LENGTH_SHORT).show();
                    }else{
                        int fixedPageNumber = pageNumber + 1;
                        SharedPrefs.save(BookView.this, SharedPrefs.SHARED_BOOK_MARKS_FILE, SharedPrefs.KEY_BOOK_MARKED_PAGE_NUMBER + id, fixedPageNumber);
                        item.setIcon(R.drawable.ic_bookmark_filled);
                        Toast.makeText(BookView.this, "marked bookmark", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    //todo add the google play store link to the share message
                    String shareBody = "Hey, I am reading this. If you want to read it download the app.\n\n*" + bookName + "*\n\n*Read on $app name eBooks:*\n$play store link";
                    String shareSubject = "Shareable link";

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);

                    //incrementing book shares counter in case we want to display it later in the book details
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                    final Query query = reference.orderByChild("bookId").equalTo(id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot != null) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Book books = snapshot1.getValue(Book.class);
                                    if (snapshot1.child("bookShares").exists()) {
                                        String share = books.getBookShares();
                                        int integerReader = (Integer.parseInt(share)) + 1;
                                        books.setBookShares(String.valueOf(integerReader));
                                        reference.child(id).setValue(books);
                                    } else {
                                        books.setBookShares("1");
                                        reference.child(id).setValue(books);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                    break;

                default:
                    break;

            }

            return true;
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


}