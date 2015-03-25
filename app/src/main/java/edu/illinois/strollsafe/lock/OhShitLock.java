package edu.illinois.strollsafe.lock;

import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.illinois.strollsafe.util.AppStorage;

/**
 * @author Noah Prince
 */
public class OhShitLock {
    private String key;
    private static OhShitLock instance;
    private boolean isLocked = false;
    private static final String SALT = "tA1";

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
        return new String(hash(pass)).equals(key);
    }

    public boolean restorePass(Context context){
        // Restore preferences
        key = AppStorage.getInstance().retrieveSetting(context, "pin");
        // There was no stored password
       return key != null;
    }

    public void setPass(Context context, String pass){
        key = new String(hash(pass));
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        AppStorage.getInstance().storeSetting(context, "pin", key);
    }

    private byte[] hash(String pin) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bytes = (pin + SALT).getBytes();
            return sha256.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
