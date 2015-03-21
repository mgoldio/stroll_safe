package edu.illinois.strollsafe.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;

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
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
    }
}
