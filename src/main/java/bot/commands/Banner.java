package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

public class Banner extends BotCommand {

    public Banner(String name) {
        super("{cmd} [user]", name);
    }

    @Override
    public void run(Message message, String[] args) {

        Member member = message.getMember();
        Guild guild = message.getGuild();
        TextChannel channel = message.getChannel().asTextChannel();
        MessageCreateBuilder send = new MessageCreateBuilder();

        send.setContent(member.getAsMention());

        Bot.fetchUser(args.length < 1 ? member.getId() : args[0]).queue(m -> m.retrieveProfile().queue(p -> {
            String banner = p.getBannerUrl() == null
                    ? null
                    : p.getBannerUrl() + "?size=2048";

            if (banner == null) {
                Bot.tempMessage(channel, "Nenhum banner foi encontrado para este usuário.", 10000);
                return;
            }

            send.setEmbeds(embed(banner, guild, m));
            channel.sendMessage(send.build()).queue();
        }, e -> Bot.tempEmbed(channel, Responses.ERROR_USER_NOT_FOUND, 10000)));
    }

    private MessageEmbed embed(String url, Guild guild, User target) {
        final EmbedBuilder builder = new EmbedBuilder();

        // Embed related
        String title = "🖼 " + target.getGlobalName();
        Color color = new Color(88, 101, 242);

        switch (target.getId()) {

            // Custom stuff for Anjo
            case "742729586659295283" -> {
                color = new Color(148, 0, 211);
                title = "🍑 " + target.getName();
            }

            // Custom stuff for Myuu (main)
            case "183645448509194240" -> {
                color = new Color(194, 0, 0);
                title = "🍒 " + target.getName();
            }

            // Custom stuff for Myuu (alt)
            case "727978798464630824" -> {
                color = new Color(255, 51, 243);
                title = "🍒 " + target.getName();
            }
        }

        return builder
                .setTitle(title, url)
                .setDescription(String.format("Banner de `%s`", target.getName()))
                .setColor(color) // Discord 'blue color'
                .setImage(url)
                .setFooter(guild.getName(), guild.getIconUrl())
                .build();
    }
}