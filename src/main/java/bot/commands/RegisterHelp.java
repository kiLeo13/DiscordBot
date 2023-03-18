package bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class RegisterHelp {
    private RegisterHelp() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();

    }
}