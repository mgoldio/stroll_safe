package edu.illinois.strollsafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.illinois.strollsafe.lock.LockBackgroundService;
import edu.illinois.strollsafe.lock.OhShitLock;
import edu.illinois.strollsafe.util.EmergencyContacter;
import edu.illinois.strollsafe.util.PassKeyboard;
import edu.illinois.strollsafe.util.timer.AcceleratableTimer;
import edu.illinois.strollsafe.util.timer.TimedThread;
import edu.illinois.strollsafe.util.timer.Timer;

public class LockedActivity extends PassKeyboard {

    private static final AcceleratableTimer timer = new AcceleratableTimer(20000);
    private TimedThread timedThread;
    private Intent serviceIntent;

    public static Timer getLockTimer() {
        return timer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        initialize();

        OhShitLock.getInstance().setLocked(true);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

        final View lockView = findViewById(R.id.lockView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        final TextView timerText = (TextView) findViewById(R.id.timerText);
        progressBar.setIndeterminate(false);
        progressBar.setMax(1000);
        progressBar.setProgress(0);

        timer.reset();
        serviceIntent = new Intent(this, LockBackgroundService.class);
        startService(serviceIntent);
        timedThread = new TimedThread(new Runnable() {
            @Override
            public void run() {
                final int percent = (int) (((double) timer.getTimeElapsed()
                        / timer.getDuration()) * 1000) + 1;
                final double remaining = timer.getTimeRemaining() / 1000d;
                lockView.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(percent);
                        timerText.setText(String.format("%.01f", remaining));
                    }
                });
            }
        }, timer, 20L, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
        timedThread.start();
        progressBar.setOnTouchListener(new SpeedyTouchListener());
    }

    private class SpeedyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_DOWN)
                return false;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                timer.setAcceleration(25.0);
            } else {
                timer.setAcceleration(0.0);
                timer.setVelocity(0.0);
            }
            return true;
        }
    }


    public void onPinLockInserted() {
        String pass = pinCodeField1.getText().toString() + pinCodeField2.getText().toString() +
                pinCodeField3.getText().toString() + pinCodeField4.getText();

        if (OhShitLock.getInstance().checkPass(pass)) {
            if (serviceIntent != null)
                stopService(serviceIntent);

            OhShitLock.getInstance().setLocked(false);
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
    }

    protected void showPasswordError() {
        Toast toast = Toast.makeText(LockedActivity.this, getString(R.string.wrong_passcode), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (OhShitLock.getInstance().isLocked())
            EmergencyContacter.sendEmergency(this);
        if (timedThread != null)
            timedThread.forciblyStop();

        OhShitLock.getInstance().setLocked(false);
    }
}
