package bot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Lapada {
    private Lapada() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        channel.sendMessage("https://pbs.twimg.com/media/FpsspJPWYAAjwg_.jpg").queue();
        message.delete().queue();
    }
}