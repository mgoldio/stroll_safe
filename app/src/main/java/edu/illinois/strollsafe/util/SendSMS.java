//<uses-permission android:name="android.permission.SEND_SMS" /> //TODO uncomment me!
//not sure where to put this

package edu.illinois.strollsafe.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.content.Context;
import android.util.Log;

public class SendSMS { //contact MetCad? in Urbana but employee of Champaign

   public void sendSMS(Context context) {
       String destination = "13017515134"; //Noah's phone number
       String text = "Your penis is weak";
       sendEmergencySMS(context, destination, text);
   }

   private void sendEmergencySMS(Context context, String phoneNo, String message) {

       Log.i("Send SMS", "seeing where this goes");
       try {
           SmsManager smsManager = SmsManager.getDefault(); //add an "=" here
           smsManager.sendTextMessage(phoneNo, null, message, null, null);
           //might need to change last 2 nulls (intent?)
           Toast.makeText(context.getApplicationContext(), "IT WORKED!!",
                   Toast.LENGTH_LONG).show();
       } catch (Exception e) {
           Toast.makeText(context.getApplicationContext(),
            "IT DIDN'T WORKED :(", Toast.LENGTH_LONG).show();
           e.printStackTrace();
       }


     /*  Context ctx = context.getApplicationContext();
       CharSequence text = "It's a toast!";
       int duration = Toast.LENGTH_SHORT;

       Toast toast = Toast.makeText(context, text, duration);
       toast.show(); */
   }
}