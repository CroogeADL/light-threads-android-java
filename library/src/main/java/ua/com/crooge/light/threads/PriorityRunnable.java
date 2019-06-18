package ua.com.crooge.light.threads;

import android.os.Process;

/**
 * Runnable that sets priority of thread before execution
 */
public abstract class PriorityRunnable implements Runnable {
    private int threadPriority = Process.THREAD_PRIORITY_BACKGROUND;

    public PriorityRunnable() {
    }

    public PriorityRunnable(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    @Override
    public final void run() {
        Process.setThreadPriority(threadPriority);
        runImpl();
    }

    protected abstract void runImpl();

    public int getPriority() {
        return threadPriority;
    }
}
