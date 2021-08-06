package com.digitalminds.projectlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.digitalminds.projectlibrary.BookView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

//SharedPreferences manager class
public class SharedPrefs {

    public static final String GENERAL_FILE = "general";
    //shared preference files
    public static String SHARED_BOOK_MARKS_FILE = "bookmarks";

    //here you can centralize all your shared prefs keys
    public static String KEY_NIGHT_MODE = "night";
    public static String KEY_HAS_BOOK_MARK = "bookmarked";
    public static String KEY_BOOK_MARKED_PAGE_NUMBER = "pageNumber";
    
    public static String KEY_APP_LANGUAGE_ID = "language";

    public static final String APP_LANGUAGE_ENGLISH = "en";
    public static final String APP_LANGUAGE_ARABIC = "ar";
    public static final String APP_LANGUAGE_KURDISH = "ku";

    //this method gets the SharedPreferences object instance
    //or creates SharedPreferences file if not present
    private static SharedPreferences getPrefs(Context context, String fileName) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    //Save Booleans
    public static void save(Context context, String fileName, String key, boolean value) {
        getPrefs(context, fileName).edit().putBoolean(key, value).commit();
    }

    //Get Booleans
    public static boolean getBoolean(Context context, String fileName, String key) {
        return getPrefs(context, fileName).getBoolean(key, false);
    }

    //Get Booleans if not found return a predefined default value
    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {
        return getPrefs(context, fileName).getBoolean(key, defaultValue);
    }

    //Strings
    public static void save(Context context, String fileName, String key, String value) {
        getPrefs(context, fileName).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String fileName, String key) {
        return getPrefs(context, fileName).getString(key, "");
    }

    public static String getString(Context context, String fileName, String key, String defaultValue) {
        return getPrefs(context, fileName).getString(key, defaultValue);
    }

    //Integers
    public static void save(Context context, String fileName, String key, int value) {
        getPrefs(context, fileName).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String fileName, String key) {
        return getPrefs(context, fileName).getInt(key, 0);
    }

    public static int getInt(Context context, String fileName, String key, int defaultValue) {
        return getPrefs(context, fileName).getInt(key, defaultValue);
    }

    //Floats
    public static void save(Context context, String fileName, String key, float value) {
        getPrefs(context, fileName).edit().putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String fileName, String key) {
        return getPrefs(context, fileName).getFloat(key, 0);
    }

    public static float getFloat(Context context, String fileName, String key, float defaultValue) {
        return getPrefs(context, fileName).getFloat(key, defaultValue);
    }

    //Longs
    public static void save(Context context, String fileName, String key, long value) {
        getPrefs(context, fileName).edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String fileName, String key) {
        return getPrefs(context, fileName).getLong(key, 0);
    }

    public static long getLong(Context context, String fileName, String key, long defaultValue) {
        return getPrefs(context, fileName).getLong(key, defaultValue);
    }


}
