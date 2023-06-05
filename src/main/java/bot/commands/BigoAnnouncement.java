package bot.commands;

import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@CommandPermission(permissions = Permission.MESSAGE_MANAGE)
public class BigoAnnouncement implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        String announcement = String.format("""
                Vão lá conferir a live do %s!
                
                %s
                """,
                "Bigo",
                "https://www.twitch.tv/poderosobigo"
        );

        channel.sendMessage(announcement).queue();
    }
}