package bot.commands;

import bot.util.CommandExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;

public class Avatar implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String[] args = message.getContentRaw().split(" ");
        Member target;

        try {
            String targetId = args[1].replaceAll();

            target = message.getGuild().retrieveMemberById(targetId).complete();
        } catch (ArrayIndexOutOfBoundsException |  e) {
            target = member;
        }

        if (member == null) return;

        boolean isFromGuild = message.getContentRaw().endsWith("--server");
        String avatarUrl = avatarUrl(target, isFromGuild);

        channel.sendMessageEmbeds(embed(avatarUrl)).queue();
    }

    private MessageEmbed embed(String url) {
        final EmbedBuilder builder = new EmbedBuilder();
        return builder
                .setColor(new Color(88, 101, 242)) // Discord 'blue color'
                .setImage(url)
                .build();
    }

    private String avatarUrl(Member target, boolean fromGuild) {
        String avatar = fromGuild ? target.getAvatarUrl() : target.getUser().getAvatarUrl();

        // If avatar is null, well, then just return null lol
        return avatar == null ? null : avatar + "?size=2048";
    }
}