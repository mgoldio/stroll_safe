package edu.illinois.strollsafe.lock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import edu.illinois.strollsafe.LockedActivity;
import edu.illinois.strollsafe.util.EmergencyContacter;
import edu.illinois.strollsafe.util.GeneralSingletons;
import edu.illinois.strollsafe.util.timer.TimedThread;
import edu.illinois.strollsafe.util.timer.Timer;

/**
 * @author Michael Goldstein
 */
public class LockBackgroundService extends Service {

    private TimedThread timedThread;
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void startService() {
        timer = LockedActivity.getLockTimer();
        timedThread = new TimedThread(GeneralSingletons.EMPTY_RUNNABLE, timer, 50L, new Runnable() {
            @Override
            public void run() {
                EmergencyContacter.sendEmergency(getApplicationContext());
            }
        });
        timedThread.start();
    }

    private void stopService() {
        timedThread.forciblyStop();
    }
}
