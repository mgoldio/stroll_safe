package edu.illinois.strollsafe;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.strollsafe.lock.OhShitLock;
import edu.illinois.strollsafe.util.Vector;

/**
 * @author Michael Goldstein
 */
public class ShakeBackgroundService extends Service implements SensorEventListener {

    private long lastShakeUpdate = System.nanoTime();
    private List<Vector> vectorList = new ArrayList<>();
    private Vector gravity;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            gravity = new Vector(event.values);
            return;
        }

        long time;
        if (MainActivity.getMode() != Mode.SHAKE || ((time = System.nanoTime()) - lastShakeUpdate) < 50000000)
            return;

        lastShakeUpdate = time;
        boolean shakeDetected = false;
        short directionChangeCount = 0;
        Vector v = new Vector(event.values);
        v.minusGravity(gravity.getValues());
        vectorList.add(v);
        while (vectorList.size() > 8)
            vectorList.remove(0);
        float maxMagnitude = 0.0f;
        for (int i = 0; i < vectorList.size() - 1; i++) {
            Vector va = vectorList.get(i);
            float ma = va.getMagnitude();
            if (ma < 12f)
                continue;
            if (ma > maxMagnitude)
                maxMagnitude = ma;
            for (int j = i + 1; j < vectorList.size(); j++) {
                Vector vb = vectorList.get(j);
                float mb = vb.getMagnitude();
                if (mb < 12f)
                    continue;
                if (vb.pointsAwayFrom(va)) {
                    directionChangeCount++;
                }
            }
            shakeDetected = directionChangeCount >= 2 && maxMagnitude > 16.0f;
            if (shakeDetected) {
                vectorList.clear();
                break;
            }
        }


        if (shakeDetected && !OhShitLock.getInstance().isLocked()) {
            Intent intent = new Intent(this, LockedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (MainActivity.getInstance() != null)
                MainActivity.getInstance().changeMode(Mode.MAIN);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing for now
    }
}
