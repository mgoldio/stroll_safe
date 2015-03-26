package edu.illinois.strollsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import java.io.IOException;

import edu.illinois.strollsafe.lock.OhShitLock;
import edu.illinois.strollsafe.util.AppStorage;
import edu.illinois.strollsafe.util.EmergencyContacter;
import edu.illinois.strollsafe.util.location.LocationService;
import edu.illinois.strollsafe.util.timer.SimpleTimer;
import edu.illinois.strollsafe.util.timer.TimedThread;
import edu.illinois.strollsafe.util.timer.Timer;


public class MainActivity extends Activity {

    private TimedThread releasedTimedThread;
    private Intent shakeServiceIntent;
    private static Mode mode = Mode.LICENSE;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        try {
            LocationService.testLocationSupported(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (AppStorage.getInstance().retrieveSetting(this, "terms") == null) {
            setContentView(R.layout.activity_license);
            LicenseListener listener = new LicenseListener(this);
            findViewById(R.id.licenseButton).setOnTouchListener(listener);
            ((CheckBox) findViewById(R.id.licnseCheckBox)).setOnCheckedChangeListener(listener);
        } else {
            changeToMainActivity();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void showUnsupportedLocationDialog() {
        finishActivity(1010);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Stroll Safe Not Supported");
        dialog.setMessage("Stroll Safe is only supported on the University of Illinois " +
                "campus. We are going national soon, but please be patient! Sorry for t" +
                "he inconvenience.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.create();
        dialog.show();
    }

    public void changeToMainActivity() {
        setContentView(R.layout.activity_main);
        mode = Mode.MAIN;
        MainListener listener = new MainListener(this);
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        findViewById(R.id.mainLayout).setOnTouchListener(listener);
        findViewById(R.id.mainButton).setOnTouchListener(listener);
        findViewById(R.id.closeButton).setOnLongClickListener(listener);

        if (!OhShitLock.getInstance().restorePass(this)) {
            Intent intent = new Intent(this, SetLockActivity.class);
            startActivityForResult(intent, 1010);
        }

        shakeServiceIntent = new Intent(this, ShakeBackgroundService.class);
        startService(shakeServiceIntent);
    }

    public static Mode getMode() {
        return mode;
    }

    public void openOhShitLock() {
        final View mainView = findViewById(R.id.mainLayout);
        if (mode == Mode.THUMB) {
            mainView.post(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LockedActivity.class));
                    changeMode(Mode.MAIN);
                }
            });
        }
    }

    private void HandleThumbReleased() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);

        final View mainView = findViewById(R.id.mainLayout);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        final TextView middleText = (TextView) findViewById(R.id.middleText);
        final long duration = GlobalConfig.RELEASE_TIMER_DURATION; // 1.0 seconds
        final Timer timer = new SimpleTimer(duration);
        releasedTimedThread = new TimedThread(new Runnable() {
            @Override
            public void run() {
                if (mode != Mode.THUMB)
                    Thread.currentThread().interrupt();
                final int percent = (int) (((double) timer.getTimeElapsed()
                        / timer.getDuration()) * 100) + 1;
                final double remaining = (double) timer.getTimeRemaining() / timer.getDuration();
                mainView.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(percent);
                        middleText.setText(String.format("%.01f seconds remaining", remaining));
                    }
                });
            }
        }, timer, 20, new Runnable() {
            @Override
            public void run() {
                openOhShitLock();
            }
        });
        releasedTimedThread.start();
    }

    public void changeMode(Mode newMode) {
        if (mode == Mode.LICENSE)
            return;

        mode = newMode;

        if (releasedTimedThread != null)
            releasedTimedThread.forciblyStop();

        TextView headerText = (TextView) findViewById(R.id.headerText);
        TextView subText = (TextView) findViewById(R.id.subText);
        Space space1 = (Space) findViewById(R.id.space1);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageButton closeButton = (ImageButton) findViewById(R.id.closeButton);
        TextView middleText = (TextView) findViewById(R.id.middleText);
        Space space2 = (Space) findViewById(R.id.space2);
        ImageButton mainButton = (ImageButton) findViewById(R.id.mainButton);
        TextView bottomText = (TextView) findViewById(R.id.bottomText);
        Space space3 = (Space) findViewById(R.id.space3);
        int fingerId = getResources().getIdentifier("@drawable/finger_icon", null, getPackageName());
        int shakeId = getResources().getIdentifier("@drawable/shake_icon", null, getPackageName());
        switch (mode) {
            case MAIN:
                headerText.setText("Stroll Safe");
                subText.setText("Keeping You Safe on Late Night Strolls");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                middleText.setVisibility(View.GONE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                mainButton.setImageDrawable(getResources().getDrawable(fingerId));
                bottomText.setText("Press and Hold to Arm");
                bottomText.setVisibility(View.VISIBLE);
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                break;

            case RELEASE:
                headerText.setText("Release Mode");
                subText.setText("Release Thumb to Contact Police");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                middleText.setVisibility(View.GONE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                mainButton.setImageDrawable(getResources().getDrawable(shakeId));
                bottomText.setText("Slide Thumb and Release to Enter Shake Mode");
                bottomText.setVisibility(View.VISIBLE);
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3f));
                break;

            case SHAKE:
                headerText.setText("Shake Mode");
                subText.setText("Shake Phone to Contact Police");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
                middleText.setText("Press and Hold to Exit the App");
                middleText.setVisibility(View.VISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                mainButton.setImageDrawable(getResources().getDrawable(fingerId));
                bottomText.setText("Press and Hold to Enter Release Mode");
                bottomText.setVisibility(View.VISIBLE);
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                break;

            case THUMB:
                headerText.setText("Thumb Released");
                subText.setText("Press and Hold Button to Cancel");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.GONE);
                middleText.setVisibility(View.VISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                mainButton.setImageDrawable(getResources().getDrawable(fingerId));
                bottomText.setVisibility(View.GONE);
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                HandleThumbReleased();
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mode != Mode.MAIN)
            changeMode(Mode.SHAKE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mode != Mode.MAIN)
            changeMode(Mode.SHAKE);
    }

    @Override
    protected void onDestroy() {
        if (OhShitLock.getInstance().isLocked())
            EmergencyContacter.sendEmergency(this);
        if (shakeServiceIntent != null)
            stopService(shakeServiceIntent);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
        super.onDestroy();
    }

}
