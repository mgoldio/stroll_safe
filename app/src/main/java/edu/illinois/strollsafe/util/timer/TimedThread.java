package edu.illinois.strollsafe.util.timer;

/**
 * A Timed Thread that runs the specified runnable over and over until the specified amount of time
 * has elapsed or the thread is stopped.
 *
 * @author Michael Goldstein
 */
public class TimedThread {

    private final Runnable runnable;
    private final Thread thread;
    private volatile boolean isRunning = false;

    /**
     * Creates a new Timed Thread
     * @param runnable the runnable to execute over and over until the time has elapsed
     * @param millis the duration of the timed thread
     * @param waitMillis the time the thread should wait between executing the runnable
     */
    public TimedThread(Runnable runnable, long millis, long waitMillis) {
        this.runnable = createTimedRunnable(runnable, millis, waitMillis);
        thread = new Thread(runnable);
    }

    /**
     * Starts the timed thread
     */
    public void start() {
        thread.start();
        isRunning = true;
    }

    /**
     * Stops the thread
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Unsafely stops the thread
     */
    public void forciblyStop() {
        isRunning = false;
        thread.interrupt();
    }

    /**
     * @return true if the thread is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    private Runnable createTimedRunnable(final Runnable runnable, final long millis, final long waitMillis) {
        return new Runnable() {
            @Override
            public void run() {
                long startTime = System.nanoTime();
                while((System.nanoTime() - startTime) > (millis * 1000000L))
                {
                    if(!isRunning)
                        return;

                    runnable.run();
                    try {
                        Thread.sleep(waitMillis);
                    } catch (InterruptedException e) {
                        // do nothing since forciblyStop could cause this
                    }
                }
                isRunning = false;
            }
        };
    }
}
