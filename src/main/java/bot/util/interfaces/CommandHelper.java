package bot.util.interfaces;

import net.dv8tion.jda.api.entities.Message;

public interface CommandHelper {
    default void help(Message message) {
        message.getChannel().sendMessage("No help provided.").queue();
    }
}