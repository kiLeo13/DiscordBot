package bot.commands;

import bot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public final class Ping {
    private Ping() {}

    public static void run(MessageChannelUnion channel, User author) {
        JDA api = Main.getApi();

        if (author.isBot()) return;

        channel.sendMessage("Gateway ping: `" + api.getGatewayPing() + "ms`").queue();
    }
}