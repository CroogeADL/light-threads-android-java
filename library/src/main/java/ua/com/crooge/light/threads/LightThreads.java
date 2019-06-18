package ua.com.crooge.light.threads;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides methods for creating {@link ExecutorService} or {@link ScheduledExecutorService} and methods for running tasks in the background or foreground.
 */
public final class LightThreads {

    private static final int DEVICE_NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler();

    private static final ExecutorService RANDOM_THREAD_EXECUTOR = newExecutor(4, "BackgroundExecutor");
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = newScheduledExecutor(4, "ScheduledExecutor");

    private LightThreads() {
    }

    /**
     * Run task in the background. If current thread isn't UI thread, task will be executed on this thread.
     *
     * @param task command to run
     * @return future for task
     */
    public static Future runInBackground(final Runnable task) {
        return runInBackground(task, true);
    }

    /**
     * Run task in the background. If current thread isn't UI thread, task will be executed on this thread.
     *
     * @param task        command to run
     * @param immediately flag indicates that the task should be run immediately if current thread isn't UI thread
     * @return future for task
     */
    public static Future runInBackground(final Runnable task, final boolean immediately) {
        if (immediately && !isUiThread()) {
            task.run();
            return null;
        }
        return RANDOM_THREAD_EXECUTOR.submit(task);
    }

    /**
     * Run task in the background after delay.
     *
     * @param task        command to run
     * @param delayMillis delay in milliseconds
     */
    public static Future runInBackground(final Runnable task, final long delayMillis) {
        return SCHEDULED_EXECUTOR.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedule task to run in the background.
     *
     * @param task  command to run
     * @param delay delay before executing task
     * @param unit  time unit
     * @return future for task
     */
    public static Future schedule(final Runnable task, final long delay, final TimeUnit unit) {
        return SCHEDULED_EXECUTOR.schedule(task, delay, unit);
    }

    /**
     * Schedule task to run in the background periodically.
     *
     * @param task   command to run
     * @param delay  delay before executing task
     * @param period period between successive executions
     * @param unit   time unit
     * @return future for task
     */
    public static Future schedule(final Runnable task, final long delay, final long period, final TimeUnit unit) {
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, delay, period, unit);
    }

    private static Handler getMainHandler() {
        return MainThreadHandlerHolder.INSTANCE;
    }

    /**
     * Run task on UI thread. If current thread is UI thread, task will be executed immediately on this thread.
     *
     * @param task command to run
     */
    public static void runInForeground(final Runnable task) {
        if (isUiThread()) {
            task.run();
            return;
        }
        getMainHandler().post(task);
    }

    /**
     * Run a task on UI thread after delay.
     *
     * @param task        command to run
     * @param delayMillis delay in milliseconds
     */
    public static void runInForeground(final Runnable task, final long delayMillis) {
        getMainHandler().postDelayed(task, delayMillis);
    }

    private static final class MainThreadHandlerHolder {
        static final Handler INSTANCE = new Handler(Looper.getMainLooper());
    }

    /**
     * Check if current thread is UI thread.
     *
     * @return true if the thread is UI thread, otherwise - false
     */
    public static boolean isUiThread() {
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }

    /**
     * Create new {@link ExecutorService}.
     *
     * @param multiplier multiplier for number of cores. Core pool size calculated by this formula: <br />{@code corePoolSize = numberOfCores * multiplier}
     * @param threadName name used for generating threads
     * @return new instance of ExecutorService
     */
    public static ExecutorService newExecutor(final int multiplier, final String threadName) throws IllegalArgumentException {
        if (TextUtils.isEmpty(threadName)) {
            throw new IllegalArgumentException();
        }
        return new DefaultExecutor(DEVICE_NUMBER_OF_CORES * multiplier, new DefaultThreadFactory(threadName));
    }

    /**
     * Create new {@link ExecutorService}.
     *
     * @param threadName      name used for generating threads
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @return new instance of ExecutorService
     */
    public static ExecutorService newCacheExecutor(final String threadName, final int maximumPoolSize) throws IllegalArgumentException {
        if (TextUtils.isEmpty(threadName)) {
            throw new IllegalArgumentException();
        }
        return new DefaultCachedExecutor(new DefaultThreadFactory(threadName), maximumPoolSize);
    }

    /**
     * Create new {@link ScheduledExecutorService}.
     *
     * @param multiplier multiplier for number of cores. Core pool size calculated by this formula: <br />{@code corePoolSize = numberOfCores * multiplier}
     * @param threadName name used for generating threads
     * @return new instance of ScheduledExecutorService
     */
    public static ScheduledExecutorService newScheduledExecutor(final int multiplier, final String threadName) throws IllegalArgumentException {
        if (TextUtils.isEmpty(threadName)) {
            throw new IllegalArgumentException();
        }
        return new DefaultScheduledExecutor(DEVICE_NUMBER_OF_CORES * multiplier, new DefaultThreadFactory(threadName));
    }

    private static abstract class CustomExecutor extends ThreadPoolExecutor {

        CustomExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, DefaultThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return super.submit(task, result);
        }
    }

    private static abstract class CustomScheduledExecutor extends ScheduledThreadPoolExecutor {

        CustomScheduledExecutor(int corePoolSize, DefaultThreadFactory threadFactory) {
            super(corePoolSize, threadFactory);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return super.submit(task, result);
        }
    }

    private static class DefaultExecutor extends CustomExecutor {

        DefaultExecutor(int corePoolSize, DefaultThreadFactory threadFactory) {
            super(corePoolSize, corePoolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        }

        @Override
        protected void afterExecute(Runnable task, Throwable t) {
            super.afterExecute(task, t);
            EXCEPTION_HANDLER.afterExecute(task, t);
        }
    }

    private static class DefaultCachedExecutor extends CustomExecutor {

        DefaultCachedExecutor(DefaultThreadFactory threadFactory, int maximumPoolSize) {
            super(maximumPoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
        }

        @Override
        protected void afterExecute(Runnable task, Throwable t) {
            super.afterExecute(task, t);
            EXCEPTION_HANDLER.afterExecute(task, t);
        }
    }

    private static class DefaultScheduledExecutor extends CustomScheduledExecutor {

        DefaultScheduledExecutor(int corePoolSize, DefaultThreadFactory threadFactory) {
            super(corePoolSize, threadFactory);
        }

        @Override
        protected void afterExecute(Runnable task, Throwable t) {
            super.afterExecute(task, t);
            EXCEPTION_HANDLER.afterExecute(task, t);
        }
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-" + poolNumber.getAndIncrement() + "-thread";
        }

        public Thread newThread(Runnable task) {
            Thread t = new Thread(group, task, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (task instanceof PriorityRunnable) {
                t.setPriority(((PriorityRunnable) task).getPriority());
            } else if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            t.setUncaughtExceptionHandler(EXCEPTION_HANDLER);
            return t;
        }
    }

    private static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        void afterExecute(Runnable task, Throwable t) {
            if (t == null && task instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) task;
                    if (future.isDone() && !future.isCancelled()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    t = ce;
                } catch (ExecutionException ee) {
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            if (t != null) {
                uncaughtException(Thread.currentThread(), t);
            }
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }
}
