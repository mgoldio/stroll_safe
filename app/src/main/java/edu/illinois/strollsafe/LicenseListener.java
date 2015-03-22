package edu.illinois.strollsafe;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

/**
 * @author Michael Goldstein
 */
public class LicenseListener implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private Activity context;
    private Button button;

    public LicenseListener(Activity context) {
        this.context = context;
        this.button = (Button) context.findViewById(R.id.licenseButton);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        button.setEnabled(isChecked);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        context.finish();
        context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
        return true;
    }

}
