package edu.illinois.strollsafe;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

/**
 * @author Michael Goldstein
 */
public class ShakeBackgroundService extends Service implements SensorEventListener {

    private MainActivity context;
    private static long lastShakeSend = 0;
    private long lastShakeUpdate = System.nanoTime();
    private float[] prevVector = new float[3];

    public ShakeBackgroundService(MainActivity context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        System.out.println("Shake service");
        super.onCreate();
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("Shake!");
        // TODO: make a better shake algorithm
        if (context.getMode() != Mode.SHAKE)
            return;

        long time = System.nanoTime();
        if ((time - lastShakeUpdate) > 100000000) { // only check speed every 100ms
            lastShakeUpdate = time;
            float speed = (event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            float x = event.values[0] + prevVector[0];
            float y = event.values[1] + prevVector[1];
            float z = event.values[2] + prevVector[2];
            prevVector[0] = event.values[0];
            prevVector[1] = event.values[1];
            prevVector[2] = event.values[2];
            float deltaDir = (x * x + y * y + z * z);
            if (deltaDir < 400 && speed > 550 && (System.currentTimeMillis() - lastShakeSend) > 5000) {
                lastShakeSend = System.currentTimeMillis();
                context.startActivity(new Intent(context, LockedActivity.class));
                context.changeMode(Mode.MAIN);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing for now
    }
}
