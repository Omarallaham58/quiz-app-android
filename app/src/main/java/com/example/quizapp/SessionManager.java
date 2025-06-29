package com.example.quizapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {//checked !!

    private static final String PREF_NAME = "quiz_session";
    private static final String KEY_USERNAME = "username";
    //private static final String KEY_PASSWORD = "password";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final String IS_ADMIN = "is_admin";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void logIn(Context context, String username, boolean isAdmin) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_USERNAME, username);
        //editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(IS_ADMIN, isAdmin);
        editor.putBoolean(IS_LOGGED_IN, true);

        editor.apply();//better than commit
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        //editor.clear();
        editor.putBoolean(IS_LOGGED_IN,false);
        editor.putBoolean(IS_ADMIN,false);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(IS_LOGGED_IN, false);
    }

    public static String getUsername(Context context) {
        return getPrefs(context).getString(KEY_USERNAME, null);
    }

    public static boolean isAdmin(Context context) {

        return getPrefs(context).getBoolean(IS_ADMIN, false);
    }

//    public static String getPassword(Context context) {
//        return getPrefs(context).getString(KEY_PASSWORD, null);
//    }
}
