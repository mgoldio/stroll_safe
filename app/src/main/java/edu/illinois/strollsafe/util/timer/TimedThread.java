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
     * @param timer a Timer that holds the duration of the timed thread
     * @param waitMillis the time the thread should wait between executing the runnable
     */
    public TimedThread(Runnable runnable, Timer timer, long waitMillis) {
        this.runnable = createTimedRunnable(runnable, timer, waitMillis);
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

    private Runnable createTimedRunnable(final Runnable runnable, final Timer timer, final long waitMillis) {
        return new Runnable() {
            @Override
            public void run() {
                timer.reset();
                timer.start();
                while(!timer.hasElapsed())
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
