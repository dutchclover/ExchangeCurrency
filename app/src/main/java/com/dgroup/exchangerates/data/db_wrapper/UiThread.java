package com.dgroup.exchangerates.data.db_wrapper;

import android.os.Handler;
import android.os.Looper;

import com.crashlytics.android.Crashlytics;
import com.dgroup.exchangerates.BuildConfig;



public class UiThread {

    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    /**
     * Checks if we currently on UI thread. Calls safeThrow() if called not from UI thread.
     */
    public static void checkUi() {
        if (!currentThreadIsUi()) {
            if(BuildConfig.IS_USE_CRASHLYTICS){
                Crashlytics.logException(new IllegalStateException("Method should be called from UI thread."));
            }else{
                throw new IllegalStateException("Method should be called from UI thread.");
            }
        }
    }

    /**
     * Checks if we currently not on UI thread. Calls safeThrow() if called from UI thread.
     */
    public static void checkNotUi() {
        if (currentThreadIsUi()) {
            if (!currentThreadIsUi()) {
                if(BuildConfig.IS_USE_CRASHLYTICS){
                    Crashlytics.logException(new IllegalStateException("Method should not be called from UI thread."));
                }else{
                    throw new IllegalStateException("Method should not be called from UI thread.");
                }
            }
        }
    }

    public static boolean currentThreadIsUi() {
        return sUiHandler.getLooper().getThread() == Thread.currentThread();
    }

    /**
     * Executes something on UI thread. If called from UI thread then given task will be executed synchronously.
     * @param task the code that must be executed on UI thread.
     */
    public static void run(Runnable task) {
        if (sUiHandler.getLooper().getThread() == Thread.currentThread()) {
            task.run();
        } else {
            sUiHandler.post(task);
        }
    }

    /**
     * Executes something on UI thread after last message queued in the application's main looper.
     * @param task the code that must be executed later on UI thread.
     */
    public static void runLater(Runnable task) {
        runLater(task, 0);
    }

    /**
     * Executes something on UI thread after a given delay.
     * @param task the code that must be executed on UI thread after given delay.
     * @param delay The delay (in milliseconds) until the code will be executed.
     */
    public static void runLater(Runnable task, long delay) {
        sUiHandler.postDelayed(task, delay);
    }

    /**
     * Cancels execution of the given task that was previously queued with {@link #run(Runnable)},
     * {@link #runLater(Runnable)} or {@link #runLater(Runnable, long)} if it was not started yet.
     * @param task the code that must be cancelled.
     */
    public static void cancelDelayedTask(Runnable task) {
        sUiHandler.removeCallbacks(task);
    }

}
