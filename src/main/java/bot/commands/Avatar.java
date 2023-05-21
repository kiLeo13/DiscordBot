package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

@CommandPermission()
public class Avatar implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Member target = args.length < 2 ? member : Bot.member(message.getGuild(), args[1]);
        MessageCreateBuilder send = new MessageCreateBuilder();

        if (target == null) {
            Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
            return;
        }

        boolean isFromGuild = content.endsWith("--server");
        String avatarUrl = avatarUrl(target, isFromGuild);

        if (avatarUrl == null && content.endsWith("--server")) {
            Bot.tempMessage(channel, "O usuário não possui um avatar específico para este servidor.", 5000);
            return;
        }

        send.addEmbeds(embed(avatarUrl, target));
        send.setContent(String.format("<@%d>", member.getIdLong()));

        channel.sendMessage(send.build()).queue();
    }

    private MessageEmbed embed(String url, Member target) {
        final EmbedBuilder builder = new EmbedBuilder();
        Guild guild = target.getGuild();
        String name = target.getUser().getName();

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
                .setDescription(String.format("Avatar de `%s`", target.getEffectiveName()))
                .setColor(color) // Discord 'blue color'
                .setImage(url)
                .setFooter(guild.getName(), guild.getIconUrl())
                .build();
    }

    private String avatarUrl(Member target, boolean fromGuild) {
        String avatar = fromGuild ? target.getAvatarUrl() : target.getUser().getAvatarUrl();

        // If avatar is null, well, then just return null lol
        return avatar == null ? null : avatar + "?size=2048";
    }
}