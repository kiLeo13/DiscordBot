package bot.commands;

import bot.Main;
import bot.util.Requirements;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public final class Ping {
    private Ping() {}

    public static void run(Message message) {
        JDA api = Main.getApi();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        if (!Requirements.COMMAND_PING_CHANNELS.get().contains(channel.getIdLong())) return;
        if (author.isBot()) return;

        message.delete().queue();

        channel.sendMessage("📡**｜**<@" + author.getIdLong() + "> **Oie!**\n" +
                "⏱**｜Gateway Ping**: `" + gatewayPing + "ms`\n" +
                "⚙**｜API Ping**: `" + apiPing + "ms`").queue();
    }
}