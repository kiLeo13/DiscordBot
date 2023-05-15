package bot.commands;

import bot.Main;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class Ping implements CommandExecutor {

    @Override
    public void run(Message message) {

        JDA api = Main.getApi();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();

        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        channel.sendMessage(String.format("""
                <@%s> **Oie!** <:Hiro:855653864693694494>
                
                🕒** | Gateway Ping**: `%d`ms
                📡** | API Ping**: `%d`ms
                """, author.getId(), gatewayPing, apiPing)).queue();
    }
}