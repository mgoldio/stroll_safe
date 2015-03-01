package edu.illinois.strollsafe.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.view.Gravity;
import android.widget.Toast;

import java.util.concurrent.Callable;

import edu.illinois.strollsafe.MainActivity;
import edu.illinois.strollsafe.R;
import edu.illinois.strollsafe.SetLockActivity;
import static edu.illinois.strollsafe.util.SendSMS;

public class BackgroundService extends Service {
    private boolean quit;
    private MyBinder binder = new MyBinder();

    // Timer stuff
    private static final long LOCK_TIME = 20;
    private static final long SLEEP_TIME = 20;
    private static int accelerator = 0;
    private static long lastTime;
    private static long accumulated;
    private static boolean accelerated = false;

    // The class that will feed the ui our count data
    public class MyBinder extends Binder{

        public long getRemainingTime(){
            return lastTime;
        }

        // Pass in a rando function to be run as a thread.
        public void startThread(final Callable<Void> func){
            new Thread() {
                public void run() {
                    while(!quit){
                        try {
                            func.call();
                        }catch(Exception e){

                        }
                    }
                 }
            }.start();
        }


        public void trackTime(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long durationNanos = LOCK_TIME * 1000000000L; // seconds
                    long startTime = System.nanoTime();
                    // start updating our time count unless we get quitted on
                    while (System.nanoTime() + accumulated <= (startTime + durationNanos) && !quit) {
                        long elapsed = System.nanoTime() - startTime;

                        // Optional acceleration stuff
                        long offset = (((elapsed + accumulated) - lastTime) * accelerator);
                        elapsed += offset;
                        elapsed += accumulated;
                        setLastTime(elapsed);
                        setAccumulated(accumulated + offset);
                        if (accelerated) {
                            setAccelerator(++accelerator);
                        }

                        try {
                            Thread.sleep(SLEEP_TIME, 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    sendSMS(this);
                    setAccelerated(false);
                    setLastTime(0);
                    setAccumulated(0);
                }
            }).start();
        }

       public void setAccumulated(long acc){
            synchronized(MyBinder.class){
                accumulated = acc;
            }
       }

        public void setLastTime(long newTime){
            synchronized(MyBinder.class){
                lastTime = newTime;
            }
        }

        public void setAccelerator(int accel){
            synchronized(MyBinder.class){
                accelerator = accel;
            }
        }

        public void setAccelerated(boolean a){
            synchronized(MyBinder.class){
                accelerated = a;
            }
        }
    }

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Service binded");

        return binder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("Created the service");

    }

    // invoke when the service unbind
    @Override
    public boolean onUnbind(Intent intent)
    {
        System.out.println("Service is Unbinded");
        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.quit = true;
        System.out.println("Service is Destroyed");
    }

    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
        this.quit = true;
        System.out.println("Service is ReBinded");
    }
}
