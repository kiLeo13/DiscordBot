package bot.commands;

import bot.Main;
import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class Ping extends BotCommand {

    public Ping(String name) {
        super(null, name);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        User author = message.getAuthor();

        MessageCreateBuilder builder = new MessageCreateBuilder();

        builder.setContent(author.getAsMention());
        builder.setEmbeds(embed(message));

        channel.sendMessage(builder.build()).queue();
    }

    private MessageEmbed embed(Message call) {
        final EmbedBuilder builder = new EmbedBuilder();

        JDA api = Main.getApi();
        long apiPing = api.getRestPing().complete();
        long gatewayPing = api.getGatewayPing();
        User user = call.getAuthor();
        Guild guild = call.getGuild();

        builder
                .setAuthor("📡 Overall Ping", null, user.getAvatarUrl())
                .addField("🌐 Gateway Ping", "`" + gatewayPing + "`", true)
                .addField("🌐 API Ping", "`" + apiPing + "`", true)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }
}