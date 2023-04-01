package bot.util;

import net.dv8tion.jda.api.entities.Message;

public interface Command {
    void help(Message message);

    void run(Message message);
}