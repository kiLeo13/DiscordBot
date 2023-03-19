package bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Maconha {
    private Maconha() {}

    public static void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        User author = message.getAuthor();

        if (author.isBot()) return;

        message.delete().queue();
        channel.sendMessage("https://tenor.com/view/cigar-smoke-funny-gif-25177516").queue();
    }
}