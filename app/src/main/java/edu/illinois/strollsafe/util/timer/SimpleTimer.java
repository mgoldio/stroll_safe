package edu.illinois.strollsafe.util.timer;

/**
 * A collection of methods useful for timing things
 *
 * @author Michael Goldstein
 */
public class SimpleTimer implements Timer {

    private final long duration;

    private long startNanos;
    private boolean started;
    private boolean paused;
    private long pausedStartNanos;
    private long pausedNanos;

    /**
     * @param duration the duration, in millis
     */
    public SimpleTimer(long duration) {
        this.duration = duration;
    }

    /**
     * Starts the timer
     */
    public void start() {
        started = true;
        startNanos = System.nanoTime();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        started = false;
        paused = false;
    }

    /**
     * Pauses the timer, keeping the time already elapsed
     */
    public void pause() {
        paused = true;
        pausedStartNanos = System.nanoTime();
    }

    /**
     * Resumes the timer
     */
    public void resume() {
        paused = false;
        pausedNanos += System.nanoTime() - pausedStartNanos;
    }

    /**
     * Resets the timer; an alias for stop()
     */
    public void reset() {
        stop();
    }

    /**
     * @return the time remaining on the timer, in millis
     */
    public long getTimeRemaining() {
        if(!started)
            return duration;

        long pausedNanosCur = 0L;
        if(paused)
            pausedNanosCur = System.nanoTime() - pausedStartNanos;

        return (System.nanoTime() - pausedNanosCur - pausedNanos - startNanos) / 1000000L;
    }

    /**
     * @return true if the timer is started, not paused, and has not elapsed
     */
    public boolean isRunning() {
        return started && !paused && !hasElapsed();
    }

    /**
     * @return true if the timer's duration has elapsed
     */
    public boolean hasElapsed() {
        return getTimeRemaining() <= 0;
    }
}
