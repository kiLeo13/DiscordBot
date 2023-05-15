package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class Banner implements CommandExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();

        User target = args.length < 2 ? member.getUser() : Bot.findUser(args[1]);

        if (target == null) {
            Bot.sendGhostMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000);
            return;
        }

        String banner = target.retrieveProfile().complete().getBannerUrl();

        if (banner == null) {
            Bot.sendGhostMessage(channel, "O usuário não possui um banner ou nenhum foi encontrado.", 10000);
            return;
        }

        banner += "?size=2048";

        channel.sendMessageEmbeds(embed(banner, message.getGuild(), target)).queue();
    }

    private MessageEmbed embed(String url, Guild guild, User target) {
        final EmbedBuilder builder = new EmbedBuilder();
        String name = target.getName();
        String nick;

        try {
             Member member = guild.retrieveMemberById(target.getIdLong()).complete();
             nick = member.getEffectiveName();
        } catch (ErrorResponseException | NullPointerException e) {
            nick = name;
        }

        // Embed related
        String title = "🖼 " + name;
        Color color = new Color(88, 101, 242);

        switch (target.getId()) {

            // Custom stuff for Anjo
            case "742729586659295283" -> {
                color = new Color(148, 0, 211);
                title = "\\🍑 " + name;
            }

            // Custom stuff for Myuu (main)
            case "183645448509194240" -> {
                color = new Color(194, 0, 0);
                title = "🍒 " + name;
            }

            // Custom stuff for Myuu (alt)
            case "727978798464630824" -> {
                color = new Color(255, 51, 243);
                title = "🍒 " + name;
            }
        }

        return builder
                .setTitle(title, url)
                .setDescription(String.format("Banner de `%s`", nick))
                .setColor(color) // Discord 'blue color'
                .setImage(url)
                .setFooter(guild.getName(), guild.getIconUrl())
                .build();
    }
}