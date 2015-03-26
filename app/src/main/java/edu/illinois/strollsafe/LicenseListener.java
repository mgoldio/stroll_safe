package edu.illinois.strollsafe;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import edu.illinois.strollsafe.util.AppStorage;

/**
 * @author Michael Goldstein
 */
public class LicenseListener implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private MainActivity context;
    private Button button;

    public LicenseListener(MainActivity context) {
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

        context.changeToMainActivity();
        AppStorage.getInstance().storeSetting(context, "terms", "1");
        return true;
    }

}
