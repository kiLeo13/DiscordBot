package bot.util;

import net.dv8tion.jda.api.entities.Message;

public interface CommandExecutor extends CommandHelper {
    void run(Message message);
}