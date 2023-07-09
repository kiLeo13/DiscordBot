package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

public class Avatar extends BotCommand {

    public Avatar(String name) {
        super("{cmd} [user] [--server]", name);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        String content = message.getContentRaw();
        MessageCreateBuilder send = new MessageCreateBuilder();
        boolean fromGuild = content.toLowerCase().endsWith("--server");

        send.setContent(member.getAsMention());

        // Checks what kind of avatar the member is tryna check
        // as members can set a specific avatar for each guild
        AvatarState state = resolveAvatar(args, fromGuild);
        String url;
        MessageEmbed embed;

        switch (state) {
            case SELF_AVATAR -> {
                url = member.getUser().getAvatarUrl();

                if (url == null) {
                    Bot.tempEmbed(channel, Responses.ERROR_INFORMATION_NOT_FOUND, 10000);
                    return;
                }

                embed = embed(url, guild, member.getUser());
            }

            case SELF_AVATAR_GUILD -> {
                url = member.getAvatarUrl();

                if (url == null) {
                    Bot.tempEmbed(
                            channel,
                            Responses.error("❌ Você não possui um avatar específico para este servidor.", null, null),
                            10000
                    );
                    return;
                }

                embed = embed(url, guild, member.getUser());
            }

            case MEMBER_AVATAR_GUILD -> {
                Bot.fetchMember(guild, args[0]).queue(m -> {
                    String avatar = getAvatar(m);

                    if (avatar == null) {
                        Bot.tempEmbed(
                                channel,
                                Responses.error("❌ O membro não possui um avatar específico para este servidor.", null, null),
                                10000
                        );
                        return;
                    }

                    MessageEmbed msgEmbed = embed(avatar, guild, m.getUser());
                    send.setEmbeds(msgEmbed);
                    channel.sendMessage(send.build()).queue();
                }, e -> Bot.tempEmbed(channel, Responses.ERROR_MEMBER_NOT_FOUND, 5000));
                return;
            }

            case USER_AVATAR -> {
                Bot.fetchUser(args[0]).queue(u -> {
                    String avatar = getAvatar(u);

                    if (avatar == null) {
                        Bot.tempEmbed(channel, Responses.ERROR_INFORMATION_NOT_FOUND, 10000);
                        return;
                    }

                    MessageEmbed msgEmbed = embed(avatar, guild, u);
                    send.setEmbeds(msgEmbed);
                    channel.sendMessage(send.build()).queue();
                }, e -> Bot.tempEmbed(channel, Responses.ERROR_USER_NOT_FOUND, 5000));
                return;
            }

            // This should be impossible but
            // Better safe than sorry?
            default -> {
                Bot.tempMessage(channel, "Nenhum padrão encontrado para procura.", 10000);
                return;
            }
        }

        send.setEmbeds(embed);
        channel.sendMessage(send.build()).queue();
    }

    private String getAvatar(User user) {
        String url = user.getAvatarUrl();

        return url == null
                ? null
                : url + "?size=2048";
    }

    private String getAvatar(Member member) {
        String url = member.getAvatarUrl();

        return url == null
                ? null
                : url + "?size=2048";
    }

    private MessageEmbed embed(String url, Guild guild, User target) {
        final EmbedBuilder builder = new EmbedBuilder();
        String name = target.getName();

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

    private AvatarState resolveAvatar(String[] args, boolean isGuild) {

        if (args.length == 0)
            return AvatarState.SELF_AVATAR;

        if (args.length == 1)
            return isGuild
                    ? AvatarState.SELF_AVATAR_GUILD
                    : AvatarState.USER_AVATAR;

        return isGuild
                ? AvatarState.MEMBER_AVATAR_GUILD
                : AvatarState.USER_AVATAR;
    }

    private enum AvatarState {
        SELF_AVATAR,
        SELF_AVATAR_GUILD,
        USER_AVATAR,
        MEMBER_AVATAR_GUILD
    }
}