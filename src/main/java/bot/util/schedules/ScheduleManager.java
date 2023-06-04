package bot.util.schedules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bot.util.interfaces.BotScheduler;

public class ScheduleManager {
    private static final Timer timer = new Timer(true);
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
     * @return An immutable {@link Map<>} object containing all the scheduled events.
     */
    public Map<Long, BotScheduler> getScheduledTasks() {
        return Collections.unmodifiableMap(runnables);
    }

    /**
     * You should only call this once.
     * Calling this method multiple times will lead to multiple events running multiple times.
     */
    public void release() {
        runnables.forEach((k, v) -> {
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    v.perform();
                }
            };

            timer.scheduleAtFixedRate(task, 0, k);
        });
    }
}