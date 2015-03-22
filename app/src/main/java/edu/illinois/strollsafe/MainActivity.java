package edu.illinois.strollsafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import edu.illinois.strollsafe.lock.OhShitLock;
import edu.illinois.strollsafe.util.EmergencyContacter;
import edu.illinois.strollsafe.util.timer.SimpleTimer;
import edu.illinois.strollsafe.util.timer.TimedThread;
import edu.illinois.strollsafe.util.timer.Timer;


public class MainActivity extends Activity {
    private TimedThread releasedTimedThread;

    private static Mode mode = Mode.MAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // debug
        // String PREFS_NAME = "StrollSafePrefs";
        // SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // SharedPreferences.Editor editor = settings.edit();
        //editor.remove("key");
        //editor.commit();

        if(!OhShitLock.getInstance().restorePass(this)) {
            Intent intent = new Intent(this, SetLockActivity.class);
            startActivity(intent);
        }

        // DEBUG DO NOT UNCOMMENT
        // EmergencyContacter.makeEmergencyCall(this);
        
        MainListener listener = new MainListener(this);
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        findViewById(R.id.mainLayout).setOnTouchListener(listener);
        findViewById(R.id.mainButton).setOnTouchListener(listener);
        findViewById(R.id.closeButton).setOnLongClickListener(listener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Mode getMode() {
        return mode;
    }

    public void openOhShitLock() {
        final View mainView = findViewById(R.id.mainLayout);
        if(mode == Mode.THUMB) {
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
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        final TextView middleText = (TextView)findViewById(R.id.middleText);
        final long duration = 1000L; // 1.0 seconds
        final Timer timer = new SimpleTimer(duration);
        releasedTimedThread = new TimedThread(new Runnable() {
            @Override
            public void run() {
                if(mode != Mode.THUMB)
                    Thread.currentThread().interrupt();
                final int percent = (int)(((double)timer.getTimeElapsed()
                        / timer.getDuration()) * 100) + 1;
                final double remaining = timer.getTimeRemaining() / 1000d;
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
        mode = newMode;

        if(releasedTimedThread != null)
            releasedTimedThread.forciblyStop();

        TextView headerText = (TextView)findViewById(R.id.headerText);
        TextView subText = (TextView)findViewById(R.id.subText);
        Space space1 = (Space)findViewById(R.id.space1);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        TextView middleText = (TextView)findViewById(R.id.middleText);
        Space space2 = (Space)findViewById(R.id.space2);
        ImageButton mainButton = (ImageButton)findViewById(R.id.mainButton);
        TextView bottomText = (TextView)findViewById(R.id.bottomText);
        Space space3 = (Space)findViewById(R.id.space3);
        int fingerId = getResources().getIdentifier("@drawable/finger_icon", null, getPackageName());
        int shakeId = getResources().getIdentifier("@drawable/shake_icon", null, getPackageName());
        switch(mode) {
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
    protected void onStop(){
        // TODO start SHAKE SERVICE!!!
        if(mode == Mode.RELEASE || mode == Mode.THUMB) {
            changeMode(Mode.SHAKE);
        }
        super.onStop();
    }

    @Override
    protected void onPause(){
        // TODO start SHAKE SERVICE!!
        if(mode == Mode.RELEASE || mode == Mode.THUMB) {
            changeMode(Mode.SHAKE);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(OhShitLock.getInstance().isLocked())
            EmergencyContacter.sendEmergency(this);
        super.onDestroy();
    }

}
