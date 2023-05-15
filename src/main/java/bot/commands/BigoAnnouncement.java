package bot.commands;

import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.HashMap;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class BigoAnnouncement implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        String announcement = getAnnouncement("""
                Vão lá conferir a live do <streamer>!
                
                <link>
                """);

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