package edu.illinois.strollsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;

import java.io.IOException;

import edu.illinois.strollsafe.util.location.LocationService;

public class LicenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (!LocationService.isCurrentLocationSupported(this)) {
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
            else{
                setContentView(R.layout.activity_license);
                LicenseListener listener = new LicenseListener(this);
                findViewById(R.id.licenseButton).setOnTouchListener(listener);
                ((CheckBox) findViewById(R.id.licnseCheckBox)).setOnCheckedChangeListener(listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
