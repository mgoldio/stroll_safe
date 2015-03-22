package edu.illinois.strollsafe;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * @author Michael Goldstein
 */
public class MainListener implements View.OnTouchListener, View.OnLongClickListener {

    private MainActivity context;

    public MainListener(MainActivity context) {
        this.context = context;
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