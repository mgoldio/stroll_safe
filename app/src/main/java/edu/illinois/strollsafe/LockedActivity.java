package edu.illinois.strollsafe;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
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

import edu.illinois.strollsafe.util.BackgroundService;
import edu.illinois.strollsafe.util.OhShitLock;
import edu.illinois.strollsafe.util.PassKeyboard;

public class LockedActivity extends PassKeyboard {
    private static final long LOCK_TIME = 20;
    private static final long SLEEP_TIME = 20;

    private BackgroundService.MyBinder binder;

    // define a ServiceConnection object
    private ServiceConnection conn = new ServiceConnection()
    {
        // then the Activity connected with the Service, this will be called
        @Override
        public void onServiceConnected(ComponentName name
                , IBinder service)
        {
            System.out.println("--Service Connected--");
            // achieve MyBinder instance
            binder = (BackgroundService.MyBinder) service;
        }
        // then the connection break off
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            System.out.println("--Service Disconnected--");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        initialize();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

        final View lockView = findViewById(R.id.lockView);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        final TextView timerText = (TextView)findViewById(R.id.timerText);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(20);
        Intent intent = null;
        // Connect to our count-y service thingamabob
        try{
            intent = new Intent(this, Class.forName(BackgroundService.class.getName()));
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        startService(intent);
        bindService(intent , conn , Service.BIND_AUTO_CREATE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (binder == null){
                    try {
                        Thread.sleep(SLEEP_TIME, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                binder.trackTime();

                while(binder.getRemainingTime() <= (LOCK_TIME*1000000000L)) {
                    long elapsed = binder.getRemainingTime();
                    final int percent = (int)(((double)elapsed / (LOCK_TIME*1000000000L)) * 100);
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

                finish();
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
                binder.setAccelerated(false);
                binder.setAccelerator(0);
            } else {
                binder.setAccelerated(true);
            }
            return true;
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
