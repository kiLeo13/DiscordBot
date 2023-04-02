package bot.util;

import net.dv8tion.jda.api.entities.Message;

public interface CommandExecutor {
    void run(Message message);

    default void help(Message message) {}
}