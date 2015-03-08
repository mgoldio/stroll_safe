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
     * @param millis
     * @param waitMillis
     */
    public TimedThread(Runnable runnable, long millis, long waitMillis) {
        this.runnable = createTimedRunnable(runnable, millis, waitMillis);
        thread = new Thread(runnable);
    }

    public void start() {
        thread.start();
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
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
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
