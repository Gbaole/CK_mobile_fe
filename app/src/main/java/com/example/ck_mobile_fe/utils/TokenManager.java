package com.example.ck_mobile_fe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "LoginPref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String token, String name, String email, String avatar, String userId, String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AVATAR, avatar);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    // Getter
    public String getName() { return sharedPreferences.getString(KEY_NAME, "Gamer"); }
    public String getEmail() { return sharedPreferences.getString(KEY_EMAIL, ""); }
    public String getAddress() { return sharedPreferences.getString(KEY_ADDRESS, ""); }

    public String getAvatar() { return sharedPreferences.getString(KEY_AVATAR, ""); }
    public String getUserId() { return sharedPreferences.getString(KEY_USER_ID, ""); }
    public String getToken() { return sharedPreferences.getString(KEY_TOKEN, null); }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}