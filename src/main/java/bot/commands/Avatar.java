package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@CommandPermission()
public class Avatar extends BotCommand {

    public Avatar(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        String content = message.getContentRaw();
        MessageCreateBuilder send = new MessageCreateBuilder();
        boolean fromGuild = content.toLowerCase().endsWith("--server");

        send.setContent(member.getAsMention());

        if (args.length < 1) {
            String avatarUrl = avatarUrl(member, fromGuild);

            send.setEmbeds(embed(avatarUrl, member));
            channel.sendMessage(send.build()).queue();
        } else {
            Bot.fetchMember(guild, fromGuild ? member.getId() : args[0]).queue(m -> {
                String avatarUrl = avatarUrl(m, fromGuild);

                if (avatarUrl == null) {
                    Bot.tempMessage(channel, "O usuário não possui um avatar específico para este servidor.", 5000);
                    return;
                }

                send.setEmbeds(embed(avatarUrl, m));
                channel.sendMessage(send.build()).queue();
            }, e -> Bot.tempMessage(channel, "Membro não encontrado! Talvez você esteja procurando um usuário que não está neste servidor, garanto que este recurso está sendo desenvolvido.", 15000));
        }
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

        // Return null if not found, resize the image otherwise
        return avatar == null ? null : avatar + "?size=2048";
    }
}