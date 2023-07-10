package bot.commands;

import bot.Main;
import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
        JDA api = Main.getApi();
        MessageCreateBuilder builder = new MessageCreateBuilder();
        long gatewayPing = api.getGatewayPing();

        builder.setContent(author.getAsMention());

        api.getRestPing().queue(ping -> {
            builder.setEmbeds(embed(ping, gatewayPing));
            channel.sendMessage(builder.build()).queue();
        });
    }

    private MessageEmbed embed(long restPing, long gatewayPing) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("📡 Overall Ping")
                .addField("🌐 Gateway Ping", "`" + gatewayPing + "ms`", true)
                .addField("🌐 API Ping", "`" + restPing + "ms`", true);

        return builder.build();
    }
}