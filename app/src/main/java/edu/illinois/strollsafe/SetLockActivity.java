package edu.illinois.strollsafe;

import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import edu.illinois.strollsafe.lock.OhShitLock;
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

        TextView headerText = (TextView)findViewById(R.id.headerText);

        if (pass0 == null) { // This is the first password
            pass0 = pass;
            headerText.setText("Re-enter Passcode:");
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

            headerText.setText("Enter Passcode:");
        }

        return;
    }

    protected void showPasswordError() {
        Toast toast = Toast.makeText(SetLockActivity.this, "Passcodes did not match, try again", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }

}
