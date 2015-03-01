package edu.illinois.strollsafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.illinois.strollsafe.util.OhShitLock;
import edu.illinois.strollsafe.util.PassKeyboard;

public class LockedActivity extends PassKeyboard {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        initialize();
    }

    public void onPinLockInserted(){
        String pass = pinCodeField1.getText().toString() + pinCodeField2.getText().toString() +
                pinCodeField3.getText().toString() + pinCodeField4.getText();

        if( OhShitLock.getInstance().checkPass(pass) ) {
            finish();
        } else {
            Thread shake = new Thread() {
                public void run() {
                    Animation shake = AnimationUtils.loadAnimation(LockedActivity.this, R.anim.shake);
                    findViewById(R.id.AppUnlockLinearLayout1).startAnimation(shake);
                    showPasswordError();
                    pinCodeField1.setText("");
                    pinCodeField2.setText("");
                    pinCodeField3.setText("");
                    pinCodeField4.setText("");
                    pinCodeField1.requestFocus();
                }
            };
            runOnUiThread(shake);
        }

        return;
    }

    protected void showPasswordError(){
        Toast toast = Toast.makeText(LockedActivity.this, getString(R.string.wrong_passcode), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }
}
