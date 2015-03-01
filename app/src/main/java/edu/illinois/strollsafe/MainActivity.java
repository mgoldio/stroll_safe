package edu.illinois.strollsafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import edu.illinois.strollsafe.util.OhShitLock;


public class MainActivity extends Activity {
    public static final String PREFS_NAME = "StrollSafePrefs";

    enum Mode {
        MAIN, RELEASE, SHAKE, THUMB
    }

    private static Mode mode = Mode.MAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // debug
       /* SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("key");
        editor.commit();*/

        if(!OhShitLock.getInstance().restorePass(this)){
            Intent intent = new Intent(this, SetLockActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);
        MyTouchListener listener = new MyTouchListener();
        findViewById(R.id.mainLayout).setOnTouchListener(listener);
        findViewById(R.id.mainButton).setOnTouchListener(listener);
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

    private void HandleThumbReleased() {
        final View mainView = findViewById(R.id.mainLayout);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        final TextView timerText = (TextView)findViewById(R.id.timerText);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long durationNanos = 1500000000L; // two seconds
                long startTime = System.nanoTime();
                while(System.nanoTime() <= (startTime + durationNanos)) {
                    if(mode != Mode.THUMB)
                        return;

                    final long elapsed = System.nanoTime() - startTime;
                    final int percent = (int)(((double)elapsed / durationNanos) * 100);
                    final double remaining = (double)elapsed / 1000000000L;
                    mainView.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(percent);
                            timerText.setText(String.format("%.02f seconds remaining", 2d - remaining));
                        }
                    });
                    try {
                        Thread.sleep(20, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
        }).start();
        progressBar.setProgress(0);
    }

    private void changeMode(Mode newMode) {
        mode = newMode;
        TextView headerText = (TextView)findViewById(R.id.headerText);
        TextView subText = (TextView)findViewById(R.id.subText);
        Space space1 = (Space)findViewById(R.id.space1);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        TextView timerText = (TextView)findViewById(R.id.timerText);
        Space space2 = (Space)findViewById(R.id.space2);
        ImageButton mainButton = (ImageButton)findViewById(R.id.mainButton);
        TextView bottomText = (TextView)findViewById(R.id.bottomText);
        Space space3 = (Space)findViewById(R.id.space3);
        switch(mode) {
            case MAIN:
                headerText.setText("Stroll Safe");
                subText.setText("Keeping You Safe on Late Night Strolls");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.INVISIBLE);
                timerText.setVisibility(View.INVISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                // TODO: change mainButton image to fingerprint
                bottomText.setVisibility(View.VISIBLE);
                bottomText.setText("Press and Hold to Arm");
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                break;

            case RELEASE:
                headerText.setText("Release Mode");
                subText.setText("Release Thumb to Contact Police");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.INVISIBLE);
                timerText.setVisibility(View.INVISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                // TODO: change mainButton image to shaking phone
                bottomText.setVisibility(View.VISIBLE);
                bottomText.setText("Slide Thumb and Release to Enter Shake Mode");
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 3f));
                break;

            case SHAKE:
                headerText.setText("Shake Mode");
                subText.setText("Shake Phone to Contact Police");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.INVISIBLE);
                timerText.setVisibility(View.INVISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                // TODO: change mainButton image to fingerprint
                bottomText.setVisibility(View.VISIBLE);
                bottomText.setText("Press and Hold to Enter Release Mode");
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                break;

            case THUMB:
                headerText.setText("Thumb Released");
                subText.setText("Press and Hold Button to Cancel");
                space1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                progressBar.setVisibility(View.VISIBLE);
                timerText.setVisibility(View.VISIBLE);
                space2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
                // TODO: change mainButton image to fingerprint
                bottomText.setVisibility(View.INVISIBLE);
                space3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f));
                HandleThumbReleased();
                break;
        }
    }

    private class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_DOWN)
                return false;

            Rect buttonBounds = new Rect();
            findViewById(R.id.mainButton).getDrawingRect(buttonBounds);
            boolean isWithinButtonBounds = buttonBounds.contains((int)event.getX(), (int)event.getY());
            switch(mode) {
                case MAIN:
                    if(!(v instanceof ImageButton))
                        return true;
                    changeMode(Mode.RELEASE);
                    break;

                case RELEASE:
                    if(isWithinButtonBounds)
                    {
                        changeMode(Mode.SHAKE);
                        return true;
                    }
                    changeMode(Mode.THUMB);
                    break;
                case THUMB:
                    if(!(v instanceof ImageButton) || !isWithinButtonBounds)
                        return true;
                    changeMode(Mode.RELEASE);
                    break;
                case SHAKE:
                    if(!isWithinButtonBounds)
                        return true;
                    changeMode(Mode.RELEASE);
                    break;
            }
            return true;
        }
    }
}
