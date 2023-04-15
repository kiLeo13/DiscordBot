package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Help implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();

        Bot.sendGhostMessage(channel, "*Command in development stages.*", 10000);
    }
}