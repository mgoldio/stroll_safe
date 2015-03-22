package edu.illinois.strollsafe.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import edu.illinois.strollsafe.GlobalConfig;

public class EmergencyContacter {

    public static void sendEmergency(Context context) {
        makeEmergencyCall(context);
    }

    public static void makeEmergencyCall(Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(GlobalConfig.EMERGENCY_NUMBER));
        context.startActivity(callIntent);
    }
}
