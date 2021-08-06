package com.digitalminds.projectlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.digitalminds.projectlibrary.helper.LocaleHelper;
import com.digitalminds.projectlibrary.utils.SharedPrefs;


public class SettingsActivity extends AppCompatActivity {

    Spinner languageSpinner;

    String chosenLang;
    String currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //it fills the current language parameter and handles rtl or ltr functionality
        setUpLanguage();


        languageSpinner = findViewById(R.id.language_spinner);

        //set the cursor on the current language in case the spinner calls on Item selected
        languageSpinner.setSelection(getLanguagePosition(currentLang));

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0 && !currentLang.equals(SharedPrefs.APP_LANGUAGE_ENGLISH)){
                    chosenLang = SharedPrefs.APP_LANGUAGE_ENGLISH;
                    LocaleHelper.setLocale(getApplicationContext(), chosenLang);
                    triggerRebirth(getApplicationContext());
                }else if(position==1 && !currentLang.equals(SharedPrefs.APP_LANGUAGE_ARABIC)){
                    chosenLang = SharedPrefs.APP_LANGUAGE_ARABIC;
                    LocaleHelper.setLocale(getApplicationContext(), chosenLang);
                    triggerRebirth(getApplicationContext());
                }else if(position==2 && !currentLang.equals(SharedPrefs.APP_LANGUAGE_KURDISH)){
                    chosenLang = SharedPrefs.APP_LANGUAGE_KURDISH;
                    LocaleHelper.setLocale(getApplicationContext(), chosenLang);
                    triggerRebirth(getApplicationContext());
                }


                SharedPrefs.save(SettingsActivity.this,SharedPrefs.GENERAL_FILE, SharedPrefs.KEY_APP_LANGUAGE_ID, chosenLang);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    int getLanguagePosition(String languageId){
        //default language is english
        int position = 0;

        switch (languageId){
            case SharedPrefs.APP_LANGUAGE_ENGLISH:
                position = 0;
                break;
            case SharedPrefs.APP_LANGUAGE_ARABIC:
                position = 1;
                break;
            case SharedPrefs.APP_LANGUAGE_KURDISH:
                position = 2;
                break;
        }
        return position;
    }

    private void setUpLanguage() {

        currentLang = LocaleHelper.getLanguage(this);
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}

