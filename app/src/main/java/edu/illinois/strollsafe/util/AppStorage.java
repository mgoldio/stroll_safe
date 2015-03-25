package edu.illinois.strollsafe.util;

import android.content.Context;
import android.content.SharedPreferences;

import edu.illinois.strollsafe.GlobalConfig;

public class AppStorage {

    private static AppStorage storage;

    public static AppStorage getInstance() {
        if(storage == null)
            storage = new AppStorage();

        return storage;
    }

    public boolean storeSetting(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConfig.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String retrieveSetting(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConfig.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        return settings.getString(key, null);
    }

}
