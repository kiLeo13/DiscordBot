package bot.util;

import net.dv8tion.jda.api.entities.Message;

public interface CommandExecutor {
    void run(Message message);

    void help(Message message);
}