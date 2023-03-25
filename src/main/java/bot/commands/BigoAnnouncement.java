package bot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.HashMap;

public class BigoAnnouncement {
    private BigoAnnouncement() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String announcement = getAnnouncement("""
                Vão lá conferir a live do <streamer>!
                
                <link>
                """);

        if (author.isBot() || member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE) && member.getIdLong() != 974159685764649010L) return;

        message.delete().queue();
        channel.sendMessage(announcement).queue();
    }

    private static String getAnnouncement(String str) {
        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("<streamer>", "Bigo");
        placeholders.put("<link>", "https://www.twitch.tv/poderosobigo");

        for (String p : placeholders.keySet())
            str = str.replaceAll(p, placeholders.get(p));

        return str;
    }
}