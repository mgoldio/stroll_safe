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
    @Override
    public void start() {
        started = true;
        startNanos = System.nanoTime();
    }

    /**
     * Stops the timer
     */
    @Override
    public void stop() {
        started = false;
        paused = false;
    }

    /**
     * Pauses the timer, keeping the time already elapsed
     */
    @Override
    public void pause() {
        paused = true;
        pausedStartNanos = System.nanoTime();
    }

    /**
     * Resumes the timer
     */
    @Override
    public void resume() {
        paused = false;
        pausedNanos += System.nanoTime() - pausedStartNanos;
    }

    /**
     * Resets the timer; an alias for stop()
     */
    @Override
    public void reset() {
        stop();
    }

    /**
     * @return the time remaining on the timer, in millis
     */
    @Override
    public long getTimeRemaining() {
        if (!started)
            return duration;

        long pausedNanosCur = 0L;
        if (paused)
            pausedNanosCur = System.nanoTime() - pausedStartNanos;

        return duration - (System.nanoTime() - pausedNanosCur - pausedNanos - startNanos) / 1000000L;
    }

    /**
     * @return the time elapsed, in millis
     */
    @Override
    public long getTimeElapsed() {
        return duration - getTimeRemaining();
    }

    @Override
    public long getDuration() {
        return duration;
    }

    /**
     * @return true if the timer is started, not paused, and has not elapsed
     */
    @Override
    public boolean isRunning() {
        return started && !paused && !hasElapsed();
    }

    /**
     * @return true if the timer's duration has elapsed
     */
    @Override
    public boolean hasElapsed() {
        return getTimeRemaining() <= 0;
    }
}
