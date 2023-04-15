package bot.commands;

import bot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.util.List;

public class Userinfo implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Member target = args.length < 2 ? member : Bot.findMember(guild, args[1]);
        MessageCreateBuilder send = new MessageCreateBuilder();

        if (member == null) return;

        if (target == null) {
            Bot.sendGhostMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000);
            message.delete().queue();
            return;
        }

        send.setContent("<@" + member.getIdLong() + ">");
        send.addEmbeds(embed(target));

        channel.sendMessage(send.build()).queue();
        message.delete().queue();
    }

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

    }

    private MessageEmbed embed(Member target) {
        final EmbedBuilder builder = new EmbedBuilder();

        long creation = target.getUser().getTimeCreated().toEpochSecond();
        long joined = target.getTimeJoined().toEpochSecond();
        long boosterSince = target.getTimeBoosted() == null ? -1 : target.getTimeBoosted().toEpochSecond();
        List<Role> highest = target.getRoles();
        Role salada = target.getGuild().getRoleById(Roles.ROLE_SALADA.toId());

        // Embed stuff
        String title = "👥 " + target.getUser().getName() + "#" + target.getUser().getDiscriminator();
        String description = "Informações de `" + target.getEffectiveName() + "` <a:M_Myuu:643942157325041668>";
        Color color = highest.isEmpty() ? Color.GRAY : highest.get(0).getColor();
        String banner = target.getUser().retrieveProfile().complete().getBannerUrl();

        // Custom
        switch (target.getId()) {

            // Anjo
            case "742729586659295283" -> {
                color = new Color(148, 0, 211);
                title = "\\🍑 " + target.getUser().getName() + "#" + target.getUser().getDiscriminator();
                description = "Informações de `" + target.getEffectiveName() + "` <a:alienanjo:1094823207342719007>";
            }

            // Myuu (main)
            case "183645448509194240" -> {
                color = new Color(194, 0, 0);
                title = "🍒 " + target.getUser().getName() + "#" + target.getUser().getDiscriminator();
                description = "Informações de `" + target.getEffectiveName() + "` <a:Core_Branco:754317075635241000>";
            }

            // Myuu (alt)
            case "727978798464630824" -> {
                color = new Color(255, 51, 243);
                title = "🍒 " + target.getUser().getName() + "#" + target.getUser().getDiscriminator();
                description = "Informações de `" + target.getEffectiveName() + "` <a:Core_Branco:754317075635241000>";
            }

            default -> {
                StaffEmoji[] emojis = StaffEmoji.values();
                if (target.getRoles().contains(salada)) {
                    for (StaffEmoji e : emojis) {
                        if (e.id() == target.getIdLong()) {
                            title = e.emoji() + " " + target.getUser().getName() + "#" + target.getUser().getDiscriminator();
                            break;
                        }
                    }
                }
            }
        }

        if (banner != null) {
            banner += "?size=2048";
            builder.setImage(banner);
        }

        builder
                .setTitle(title)
                .setDescription(description)
                .setThumbnail(target.getUser().getAvatarUrl())
                .setColor(color)
                .addField("📅 Criação da Conta", String.format("<t:%d>\n<t:%d:R>", creation, creation), true)
                .addField("🌐 ID do Usuário", "`" + target.getIdLong() + "`", true)
                .addField("🌟 Entrou no Servidor", String.format("<t:%d>", joined), true)
                .setFooter("Oficina Myuu", target.getGuild().getIconUrl());

        if (boosterSince > 0) builder.addField("<:discordbooster:1094816233234378762> Booster Deste", String.format("<t:%d>", boosterSince), true);

        return builder.build();
    }
}