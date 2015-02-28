package edu.illinois.strollsafe.util;

import android.content.SharedPreferences;

/**
 * Created by noah on 2/28/15.
 */
public class OhShitLock {
    private String key;
    private static OhShitLock instance;


    protected OhShitLock(){

    }

    public static OhShitLock getInstance(){
        if (instance == null){
            instance = new OhShitLock();
            return instance;
        }

        return instance;
    }

    public boolean checkPass(String pass){
        return pass.equals(key);
    }

    public void setPass(String pass){
        key = pass;
    }
}
