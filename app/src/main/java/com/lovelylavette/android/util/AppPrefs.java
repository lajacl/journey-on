package com.lovelylavette.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.lovelylavette.android.model.User;

public class AppPrefs {
    private static final String PREFS_NAME = "com.lovelylavette.android.AppPrefs";
    private static final String USER_KEY = "user";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public AppPrefs(Context context) {
        if (settings == null) {
            settings = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
        }
        editor = settings.edit();
    }

    public void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);

        editor.putString(USER_KEY, json);
        editor.commit();
    }

    public User getUser() {
        String userStr = settings.getString(USER_KEY, null);
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);

        return user;
    }

    public void saveString(String id, String string) {
        Gson gson = new Gson();
        String json = gson.toJson(string);

        editor.putString(id, json);
        editor.commit();
    }

    public String getStringPref(String key) {
        return settings.getString(key, "");
    }

/*    public Map<String, ?> getPrefs() {
        return settings.getAll();
    }*/
}
