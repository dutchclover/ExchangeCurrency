package com.dgroup.exchangerates.data.db_wrapper;

import android.os.Process;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to encapsulate some task, that notified about it's life-cycle through callbacks
 */
public abstract class Task implements Runnable {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    private static @interface Determine {
        String value();
    }

    /**
     * Internal class to optimize calls of callbacks by determining if they are defined in subclasses
     */
    private static class TaskDescriptor {

        private static final String ON_SUCCESS = "success";
        private static final String ON_FAIL = "fail";
        private static final String ON_CANCEL = "cancel";
        private static final String ON_END = "end";

        /**
         * Methods that declared in Task
         */
        private static Map<String, Method> sOriginalMethods = new HashMap<String, Method>();

        static {
            for (Method m : Task.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Determine.class))
                    sOriginalMethods.put(m.getName(), m);
            }
        }

        /**
         * Methods that need to be called because they were overridden in subclasses
         */
        private Set<String> mOverridden = new HashSet<String>();

        /**
         * Constructs and initialize <b>TaskDescriptor</b> with subclass of <b>Task</b>
         *
         * @param cl subclass of <b>Task</b>
         */
        public TaskDescriptor(Class<? extends Task> cl) {
            Class<?> clazz = cl;
            while (clazz != Task.class) {
                for (Method m : clazz.getDeclaredMethods()) {
                    Method original = sOriginalMethods.get(m.getName());
                    //check if it is the really overriding methods (check the list of params)
                    if (original != null && Arrays.equals(original.getParameterTypes(), m.getParameterTypes())) {
                        mOverridden.add(original.getAnnotation(Determine.class).value());
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        public boolean isDetermined(String method) {
            return mOverridden.contains(method);
        }
    }

    /**
     * The cache of descriptors
     */
    private static final Map<Class<? extends Task>, TaskDescriptor> sDescriptors =
            new HashMap<Class<? extends Task>, TaskDescriptor>();

    /**
     * The exception that was thrown while executing the task
     */
    private volatile Throwable mProblem = null;

    /**
     * Flag to determine if the Task was cancelled by calling {@link Task#cancel()} ()}
     */
    private volatile boolean mCanceled = false;

    /**
     * The <b>Thread</b> which is executing the task
     */
    private Thread mOwner = null;

    /**
     * This method is <b>not</b> design to be called manually
     */
    @Override
    public final void run() {
        captureThread();
        TaskDescriptor descriptor = getDescriptor();
        mProblem = null;
        try {
            if (!mCanceled)
                onExecuteBackground();

            freeThread();

            if (!mCanceled && isDone())
                onSuccessBackground();
        } catch (Throwable e) {
            freeThread();

            mProblem = e;
            if (!mCanceled && isDone())
                onFailBackground(e);
        }
        if (mCanceled)
            onCancelBackground();
        if (isDone())
            onEndBackground();
        if (mProblem == null) {
            if (descriptor.isDetermined(TaskDescriptor.ON_SUCCESS) && isDone() && !mCanceled) {
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!mCanceled)
                            onSuccessUi();
                    }
                });
            }
        } else {
            final Throwable e = mProblem;
            if (descriptor.isDetermined(TaskDescriptor.ON_FAIL) && isDone() && !mCanceled) {
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!mCanceled)
                            onFailUi(e);
                    }
                });
            }
        }

        if (descriptor.isDetermined(TaskDescriptor.ON_CANCEL)) {
            UiThread.run(new Runnable() {
                @Override
                public void run() {
                    onCancelUi();
                }
            });
        }

        if (descriptor.isDetermined(TaskDescriptor.ON_END) && isDone()) {
            UiThread.run(new Runnable() {
                @Override
                public void run() {
                    onEndUi();
                }
            });
        }
    }

    protected int getThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    /**
     * Captures the thread that invokes the task
     */
    private void captureThread() {
        synchronized (this) {
            mOwner = Thread.currentThread();
        }
    }

    /**
     * Removes reference on invoking thread (in order to avoid leaks)
     */
    private void freeThread() {
        synchronized (this) {
            mOwner = null;
        }
    }

    /**
     * Gets the <b>TaskDescriptor</b> from cache and creates new if there is no <b>TaskDescriptor</b>
     * for this class in cache
     *
     * @return
     */
    private TaskDescriptor getDescriptor() {
        synchronized (sDescriptors) {
            Class<? extends Task> clazz = getClass();
            TaskDescriptor descriptor = sDescriptors.get(clazz);
            if (descriptor != null)
                return descriptor;

            descriptor = new TaskDescriptor(clazz);
            sDescriptors.put(clazz, descriptor);
            return descriptor;
        }
    }

    /**
     * Returns the exception that happens while invoking the task
     *
     * @return
     */
    public Throwable getLastProblem() {
        return mProblem;
    }

    /**
     * Cancels the task. Also interrupts the thread, that invokes the task.
     */
    public void cancel() {
        synchronized (this) {
            mCanceled = true;
            if (mOwner != null)
                mOwner.interrupt();
        }
    }

    /**
     * @return <b>true</b> if the task was canceled by {@link Task#cancel()}
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * Returns true if task was completely done. If returns <b>false</b>, callbacks <b>onSuccess...</b>,
     * <b>onFail...</b> and <b>onEnd...</b> will <b>not</b> be called. Actually, it is legal to return <b>true</b> even
     * if the Task wasn't start yet. It is done for simplification of the <b>run</b> method.
     *
     * @return
     */
    public boolean isDone() { return true; }

    /**
     * The actual task execution
     *
     * @throws Throwable any exception during execution
     */
    protected abstract void onExecuteBackground() throws Throwable;

    /**
     * Callback in UI-thread for successful end of task. Will be called after all <b>on...Background</b> methods, but
     * before {@link Task#onEndUi()}. Will be called only if no exceptions were happen during
     * the invocation.
     */
    @Determine(TaskDescriptor.ON_SUCCESS)
    protected void onSuccessUi() {}

    /**
     * Callback in the invoking thread (in the thread, that actually invokes
     * {@link Task#run()}) for successful end of task.
     * If any exception will be thrown during this method, the methods
     * {@link Task#onFailBackground(Throwable)} and
     * {@link Task#onFailUi(Throwable)} also will be called.
     * Will be called right after {@link Task#onExecuteBackground()} and before
     * {@link Task#onEndBackground()}. Will be called only if no exceptions were happen during
     * invocation.
     *
     * @throws Throwable
     */
    protected void onSuccessBackground() throws Throwable {}

    /**
     * Callback in UI-thread for unsuccessful end of task. Will be called after all <b>on...Background</b> methods,
     * but before {@link Task#onEndUi()}.
     */
    @Determine(TaskDescriptor.ON_FAIL)
    protected void onFailUi(Throwable e) {}

    /**
     * Callback in the invoking thread (in the thread, that actually invokes
     * {@link Task#run()}) for unsuccessful end of task. Will be called if some exception was
     * thrown during the invocation. Will be called right after
     * {@link Task#onExecuteBackground()}
     * and before {@link Task#onEndBackground()}
     */
    protected void onFailBackground(Throwable e) {}

    /**
     * Callback in UI-thread for cancelled task. Will be called after all <b>on...Background</b> methods,
     * but right before {@link Task#onEndUi()}.
     */
    @Determine(TaskDescriptor.ON_CANCEL)
    protected void onCancelUi() {}

    /**
     * Callback in the invoking thread (in the thread, that actually invokes
     * {@link Task#run()}) for canceled task. Will be called before
     * {@link Task#onEndBackground()}
     */
    protected void onCancelBackground() {}

    /**
     * Callback in UI-thread for finished task. Will be called Will be called in any case (but if isDone()
     * returns <b>true</b>).
     */
    @Determine(TaskDescriptor.ON_END)
    protected void onEndUi() {}

    /**
     * Callback in the invoking thread (in the thread, that actually invokes
     * {@link Task#run()}) for finished task. Will be called in any case (but if isDone()
     * returns <b>true</b>).
     */
    protected void onEndBackground() {}

}
