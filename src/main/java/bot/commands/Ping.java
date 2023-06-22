package bot.commands;

import bot.Main;
import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.NotNull;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class Ping extends BotCommand {

    public Ping(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {

        JDA api = Main.getApi();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();

        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        channel.sendMessage(String.format("""
                <@%s> **Oie!** <:Hiro:855653864693694494>
                
                🕒** | Gateway Ping**: `%dms`
                📡** | API Ping**: `%dms`
                """, author.getId(), gatewayPing, apiPing)).queue();
    }
}