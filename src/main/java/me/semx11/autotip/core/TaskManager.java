package me.semx11.autotip.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import me.semx11.autotip.util.ErrorReport;

public class TaskManager {

    public static final ExecutorService EXECUTOR;
    public static final ScheduledExecutorService SCHEDULER;

    private static final Map<TaskType, Future> TASKS;

    public static <T> T scheduleAndAwait(Callable<T> callable, long delay) {
        try {
            return SCHEDULER.schedule(callable, delay, TimeUnit.SECONDS).get();
        } catch (InterruptedException | ExecutionException e) {
            ErrorReport.reportException(e);
            return null;
        }
    }

    public static void executeTask(TaskType type, Runnable command) {
        if (TASKS.containsKey(type)) {
            return;
        }
        Future<?> future = EXECUTOR.submit(command);
        TASKS.put(type, future);
        catchFutureException(future);
        TASKS.remove(type);
    }

    public static void addRepeatingTask(TaskType type, Runnable command, long delay, long period) {
        if (TASKS.containsKey(type)) {
            return;
        }
        ScheduledFuture future = SCHEDULER
                .scheduleAtFixedRate(command, delay, period, TimeUnit.SECONDS);
        TASKS.put(type, future);
        catchFutureException(future);
    }

    public static void cancelTask(TaskType type) {
        if (TASKS.containsKey(type)) {
            TASKS.get(type).cancel(true);
            TASKS.remove(type);
        }
    }

    private static void catchFutureException(Future future) {
        EXECUTOR.execute(() -> {
            try {
                future.get();
            } catch (CancellationException ignored) {
                // Manual cancellation of a repeating task.
            } catch (InterruptedException | ExecutionException e) {
                ErrorReport.reportException(e);
            }
        });
    }

    private static ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder()
                .setNameFormat(name)
                .setUncaughtExceptionHandler((t, e) -> ErrorReport.reportException(e))
                .build();
    }

    public enum TaskType {
        LOGIN, KEEP_ALIVE, TIP_WAVE, TIP_CYCLE, LOGOUT
    }

    static {
        EXECUTOR = Executors.newCachedThreadPool(getThreadFactory("AutotipThread"));
        SCHEDULER = Executors.newScheduledThreadPool(3, getThreadFactory("AutotipScheduler"));
        TASKS = Collections.synchronizedMap(new HashMap<>());
    }

}
