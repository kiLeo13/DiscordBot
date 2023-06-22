package bot.misc.schedules;

import bot.internal.abstractions.BotScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private static final Map<Long, BotScheduler> runnables = new HashMap<>();
    private static ScheduleManager instance;

    private ScheduleManager() {}

    public static ScheduleManager getManager() {
        if (instance == null) instance = new ScheduleManager();
        return instance;
    }

    /**
     * Keep in mind that in order to start the schedule you must call {@link #release()} ONCE.
     * 
     * @param interval The interval between two task executions (in milliseconds).
     * @param scheduler What code should be run.
     * @return The same {@link ScheduleManager} instance for function call chaining logic.
     */
    public ScheduleManager addRunnable(long interval, BotScheduler scheduler) {
        if (scheduler == null)
            throw new IllegalArgumentException("Scheduler cannot be null");

        if (interval < 1)
            throw new IllegalArgumentException("Interval cannot be less or equal to 0");

        runnables.put(interval, scheduler);
        return this;
    }

    /**
     * You should only call this once.
     * Calling this method multiple times will lead to multiple events running multiple times.
     */
    public void release() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(runnables.size());

        runnables.forEach((k, v) -> executor.scheduleWithFixedDelay(v::perform, 1000, k, TimeUnit.MILLISECONDS));
    }
}