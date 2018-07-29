package moviesapp.udacity.com.moviesapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import moviesapp.udacity.com.moviesapp.R;

public class SharedPrefsUtil {

    private static final String APP_SHARED_PREFS_NAME = "moviesapp.udacity.com.sharedpreferences";

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, context.getResources().getString(R.string.sort_popular_option));
    }
}
