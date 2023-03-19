package bot.commands;

import bot.Main;
import bot.util.Channels;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping {
    private Ping() {}

    public static void run(Message message) {

        JDA api = Main.getApi();
        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;
        if (author.isBot()) return;

        if (!Channels.COMMAND_PING_CHANNELS.contains(channel.getIdLong())) return;

        message.delete().queue();

        channel.sendMessage("📡**｜**<@" + author.getIdLong() + "> **Oie!**\n\n" +
                "⏱**｜Gateway Ping**: `" + gatewayPing + "ms`\n" +
                "⚙**｜API Ping**: `" + apiPing + "ms`").queue();
    }

    public static void run(SlashCommandInteractionEvent e) {

        MessageChannelUnion channel = e.getChannel();
        JDA api = Main.getApi();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        if (!Channels.COMMAND_PING_CHANNELS.contains(channel.getIdLong())) return;

        e.reply("> **Pong!**\n\n" +
                        "⏱**｜Gateway Ping**: `" + gatewayPing + "ms`\n" +
                        "📡**｜API Ping**: `" + apiPing + "ms`")
                .queue();
    }
}