package com.android.myproj.minesweeper.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.android.myproj.minesweeper.config.Key;

public class MySharedPreferencesUtil {

    public static boolean contains(Activity activity, String key) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        return myPref.contains(key);
    }

    public static boolean getBoolean(Activity activity, String key, boolean defaultValue) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        return myPref.getBoolean(key, defaultValue);
    }

    public static void putBoolean(Activity activity, String key, boolean value) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        myPrefEditor.putBoolean(key, value);
        myPrefEditor.apply();
    }

    public static float getFloat(Activity activity, String key, float defaultValue) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        return myPref.getFloat(key, defaultValue);
    }

    public static void putFloat(Activity activity, String key, float value) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        myPrefEditor.putFloat(key, value);
        myPrefEditor.apply();
    }

    public static String getString(Activity activity, String key, String defaultValue) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        return myPref.getString(key, defaultValue);
    }

    public static void putString(Activity activity, String key, String value) {
        SharedPreferences myPref = activity.getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        myPrefEditor.putString(key, value);
        myPrefEditor.apply();
    }

}
