package moviesapp.udacity.com.moviesapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import moviesapp.udacity.com.moviesapp.R;

public class SharedPrefsUtil {

    private static final String APP_SHARED_PREFS_NAME = "moviesapp.udacity.com.sharedpreferences";

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, context.getResources().getBoolean(R.bool.default_sort_by_popular));
    }
}
