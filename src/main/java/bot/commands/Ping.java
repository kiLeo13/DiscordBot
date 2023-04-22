package bot.commands;

import bot.Main;
import bot.util.CommandExecutor;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        JDA api = Main.getApi();
        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        if (member == null || !member.hasPermission(Permission.MESSAGE_MANAGE)) return;
        if (author.isBot()) return;

        message.delete().queue();

        channel.sendMessage("<@" + author.getIdLong() + "> **Oie!** <:Hiro:855653864693694494>\n\n" +
                "🕒** | Gateway Ping**: `" + gatewayPing + "ms`\n" +
                "📡** | API Ping**: `" + apiPing + "ms`\n")
                .queue();
    }

    @Override
    public void runSlash(SlashCommandInteractionEvent e) {

        JDA api = Main.getApi();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();

        e.reply("> **Pong!**\n\n" +
                        "🕒** | Gateway Ping**: `" + gatewayPing + "ms`\n" +
                        "📡** | API Ping**: `" + apiPing + "ms`\n")
                .queue();
    }
}