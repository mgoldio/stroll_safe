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

public class SetLockActivity extends PassKeyboard {
    private String pass0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock);
        initialize();
    }

    public void onPinLockInserted() {
        String pass = pinCodeField1.getText().toString() + pinCodeField2.getText().toString() +
                pinCodeField3.getText().toString() + pinCodeField4.getText();

        // Reset the password display
        pinCodeField1.setText("");
        pinCodeField2.setText("");
        pinCodeField3.setText("");
        pinCodeField4.setText("");
        pinCodeField1.requestFocus();

        if (pass0 == null) { // This is the first password
            pass0 = pass;
            return;
        } else {
            if (pass0.equals(pass)) { // Confirm password
                OhShitLock.getInstance().setPass(this, pass);
                finish();
            } else {
                // Set pass to null so they can try again twice
                pass0 = null;

                Thread shake = new Thread() {
                    public void run() {
                        Animation shake = AnimationUtils.loadAnimation(SetLockActivity.this, R.anim.shake);
                        findViewById(R.id.AppUnlockLinearLayout1).startAnimation(shake);
                        showPasswordError();
                    }
                };
                runOnUiThread(shake);
            }
        }

        return;
    }

    protected void showPasswordError() {
        Toast toast = Toast.makeText(SetLockActivity.this, "Passcodes did not match, try again", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }

}
