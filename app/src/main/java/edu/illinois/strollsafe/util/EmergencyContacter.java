package edu.illinois.strollsafe.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class EmergencyContacter {

    // TODO: change to tel:911 for non-debug
    private static final String EMERGENCY_NUMBER = "tel:13017515134";

    public static void sendEmergency(Context context) {
        makeEmergencyCall(context);
    }

    public static void makeEmergencyCall(Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(EMERGENCY_NUMBER));
        context.startActivity(callIntent);
        // TODO: set speakerphone on
    }
}
