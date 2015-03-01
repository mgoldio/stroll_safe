package edu.illinois.strollsafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.illinois.strollsafe.util.OhShitLock;
import edu.illinois.strollsafe.util.PassKeyboard;

public class LockedActivity extends PassKeyboard {
    private static final long LOCK_TIME = 20;
    private static final long SLEEP_TIME = 20;
    private static int accelerator = 0;
    private static long lastTime;
    private static long accumulated;
    private static boolean accelerated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        initialize();

        final View lockView = findViewById(R.id.lockView);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        final TextView timerText = (TextView)findViewById(R.id.timerText);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(20);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long durationNanos = LOCK_TIME *1000000000L; // seconds
                long startTime = System.nanoTime();
                while(System.nanoTime()+accumulated <= (startTime + durationNanos)) {
                    long elapsed = System.nanoTime() - startTime;

                    // Optional acceleration stuff
                    long offset = (((elapsed+accumulated)-lastTime)*accelerator);
                    elapsed += offset;
                    elapsed += accumulated;
                    setLastTime(elapsed);
                    setAccumulated(accumulated+offset);
                    if (accelerated) {
                        setAccelerator(++accelerator);
                    }

                    final int percent = (int)(((double)elapsed / durationNanos) * 100);
                    final double remaining = (double)elapsed / 1000000000L;
                    lockView.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(percent);
                            timerText.setText(String.format("%.01f", LOCK_TIME - remaining));
                        }
                    });
                    try {
                        Thread.sleep(SLEEP_TIME, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                setAccelerated(false);
                setLastTime(0);
                setAccumulated(0);
            }
        }).start();
        progressBar.setProgress(0);

        progressBar.setOnTouchListener(new SpeedyTouchListener());
    }

    private class SpeedyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_DOWN)
                return false;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                setAccelerated(false);
                setAccelerator(0);
            } else {
                setAccelerated(true);
            }
            return true;
        }
    }

    private void setLastTime(long newTime){
        synchronized(LockedActivity.class){
            lastTime = newTime;
        }
    }

    private void setAccelerator(int accel){
        synchronized(LockedActivity.class){
            accelerator = accel;
        }
    }

    private void setAccelerated(boolean a){
        synchronized(LockedActivity.class){
            accelerated = a;
        }
    }

    private void setAccumulated(long acc){
        synchronized(LockedActivity.class){
            accumulated = acc;
        }
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
