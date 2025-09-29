package com.anhtu.turbo88.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "racing_session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGGED = "is_logged";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String username) {
        editor.putBoolean(KEY_LOGGED, true);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
