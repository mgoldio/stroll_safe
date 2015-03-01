package edu.illinois.strollsafe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by noah on 2/28/15.
 */
public class OhShitLock {
    private String key;
    private static OhShitLock instance;
    private boolean isLocked = false;
    public static final String PREFS_NAME = "StrollSafePrefs";

    protected OhShitLock(){
    }

    public static OhShitLock getInstance(){
        if (instance == null){
            instance = new OhShitLock();
            return instance;
        }

        return instance;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public boolean checkPass(String pass){
        return pass.equals(key);
    }

    public boolean restorePass(Context context){
        // Restore preferences
        SharedPreferences settings2 = context.getSharedPreferences(PREFS_NAME, 0);
        key = settings2.getString("key",null);

        // There was no stored password
       return key != null;
    }

    public void setPass(Context context, String pass){
        key = pass;
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("key", key);

        // Commit the edits!
        editor.commit();
    }
}
