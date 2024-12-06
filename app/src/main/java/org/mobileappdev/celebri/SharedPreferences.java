package org.mobileappdev.celebri;

import android.content.Context;

public class SharedPreferences {

    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final android.content.SharedPreferences sharedPreferences;
    private final android.content.SharedPreferences.Editor editor;

    public SharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Set user ID
    public void setUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Set login status
    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Retrieve user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Check login status
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Clear all user data
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }
}
