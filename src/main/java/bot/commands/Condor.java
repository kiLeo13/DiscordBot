package bot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class Condor {
    private Condor() {}

    public static void run(Message message) {

        List<String> words = List.of("condôr", "côndor");
        String content = message.getContentRaw();
        User author = message.getAuthor();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        Member target = null;
        int random = (int) Math.floor(Math.random() * words.size());

        if (member == null || !member.hasPermission(Permission.MESSAGE_MANAGE)) return;
        if (author.isBot()) return;

        try {
            String targetRegex = args[1].replaceAll("[^0-9]+", "");

            target = guild.retrieveMemberById(targetRegex).complete();
        } catch (ArrayIndexOutOfBoundsException ignore) {}

        if (target == null) channel.sendMessage("<@" + author.getIdLong() + "> o correto de se falar é obviamente " + words.get(random)).queue();
        else channel.sendMessage("<@" + target.getIdLong() + "> me poupe, todo mundo sabe que o certo é " + words.get(random)).queue();

        message.delete().queue();
    }
}