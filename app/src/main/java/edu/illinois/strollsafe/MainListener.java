package edu.illinois.strollsafe;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * @author Michael Goldstein
 */
public class MainListener extends PhoneStateListener implements View.OnTouchListener, View.OnLongClickListener {

    private MainActivity context;

    public MainListener(MainActivity context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if((state  & 3) > 0) {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
        }
        else {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        Rect buttonBounds = new Rect();
        context.findViewById(R.id.mainButton).getDrawingRect(buttonBounds);
        boolean isWithinButtonBounds = buttonBounds.contains((int) event.getX(), (int) event.getY());
        switch (context.getMode()) {
            case MAIN:
                if (!(v instanceof ImageButton))
                    return true;
                context.changeMode(Mode.RELEASE);
                break;

            case RELEASE:
                if (isWithinButtonBounds) {
                    context.changeMode(Mode.SHAKE);
                    return true;
                }
                context.changeMode(Mode.THUMB);
                break;
            case THUMB:
                if (!(v instanceof ImageButton) || !isWithinButtonBounds)
                    return true;
                context.changeMode(Mode.RELEASE);
                break;
            case SHAKE:
                if (!isWithinButtonBounds)
                    return true;
                context.changeMode(Mode.RELEASE);
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() != R.id.closeButton)
            return false;
        // TODO kill service
        context.finish();
        return true;
    }
}