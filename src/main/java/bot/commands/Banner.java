package bot.commands;

import bot.util.Bot;
import bot.util.content.Messages;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@CommandPermission()
public class Banner implements CommandExecutor {

    @Override
    public void run(@NotNull Message message) {

        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        TextChannel channel = message.getChannel().asTextChannel();
        MessageCreateBuilder send = new MessageCreateBuilder();

        send.setContent("<@" + member.getId() + ">");

        Bot.fetchUser(args.length < 2 ? member.getId() : args[1]).queue(m -> {
            m.retrieveProfile().queue(p -> {
                String banner = p.getBannerUrl() == null
                        ? null
                        : p.getBannerUrl() + "?size=2048";

                if (banner == null) {
                    Bot.tempMessage(channel, "Nenhum banner foi encontrado para este usuário.", 10000);
                    return;
                }

                send.setEmbeds(embed(banner, guild, m));
                channel.sendMessage(send.build()).queue();
            }, e -> Bot.tempMessage(channel, Messages.ERROR_USER_NOT_FOUND.message(), 10000));
        });
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