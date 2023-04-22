package bot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public interface CommandExecutor {
    void run(Message message);

    default MessageEmbed help(Message message) {
        return null;
    }
}