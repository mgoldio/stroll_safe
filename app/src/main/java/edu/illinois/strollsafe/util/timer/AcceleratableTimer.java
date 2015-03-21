package edu.illinois.strollsafe.util.timer;

/**
 * @author Michael Goldstein
 */
public class AcceleratableTimer extends SimpleTimer {

    private final Object lock = new Object();
    private long accelerationSetTime = 0L;
    private long addedNanos = 0L;
    private double velocity = 1.0;
    private double acceleration = 0.0;

    /**
     * @param duration the duration, in millis
     */
    public AcceleratableTimer(long duration) {
        super(duration);
    }

    @Override
    public void reset() {
        synchronized (lock) {
            super.reset();
            accelerationSetTime = 0L;
            addedNanos = 0L;
            velocity = 1.0;
        }
    }

    @Override
    public long getTimeRemaining() {
        synchronized (lock) {
            updateAccelerationNanos();
        }

        return super.getTimeRemaining() - addedNanos / 1000000;
    }

    public void setVelocity(double velocity) {
        synchronized (lock) {
            updateAccelerationNanos();
            this.velocity = velocity;
        }
    }

    public void setAcceleration(double acceleration) {
        synchronized (lock) {
            updateAccelerationNanos();
            this.acceleration = acceleration;
            accelerationSetTime = System.nanoTime();
        }
    }

    public double getAcceleration() {
        return acceleration;
    }

    private void updateAccelerationNanos() {
        if(accelerationSetTime > 0L) {
            double deltaT = (System.nanoTime() - accelerationSetTime) * 1E-9;
            double vNew = velocity + acceleration * deltaT;
            double vAve = (velocity + vNew) / 2;
            velocity = vNew;
            addedNanos += (System.nanoTime() - accelerationSetTime) * vAve;
            accelerationSetTime = System.nanoTime();
        }
    }

}
