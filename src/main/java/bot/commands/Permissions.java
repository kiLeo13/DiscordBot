package bot.commands;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

import bot.internal.abstractions.BotCommand;
import bot.util.*;
import bot.util.content.Responses;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class Permissions extends BotCommand {

    public Permissions(String... names) {
        super("{cmd} [member]", names);
    }

    @Override
    public void run(Message message, String[] args) {

        Member member = message.getMember();
        Guild guild = message.getGuild();
        TextChannel channel = message.getChannel().asTextChannel();
        MessageCreateBuilder send = new MessageCreateBuilder();

        if (args.length < 1) {
            send.setEmbeds(embed(member));
            channel.sendMessage(send.build()).queue();
        } else {
            Bot.fetchMember(guild, args[0]).queue(m -> {
                send.setEmbeds(embed(m));
                channel.sendMessage(send.build()).queue();
            }, e -> Bot.tempEmbed(channel, Responses.ERROR_MEMBER_NOT_FOUND, 10000));
        }
    }

    private MessageEmbed embed(Member target) {
        final EmbedBuilder builder = new EmbedBuilder();

        EnumSet<Permission> permissions = target.getPermissions();
        Guild guild = target.getGuild();
        Role highest = target.getRoles().get(0);
        Color color = highest == null ? null : highest.getColor();

        builder
                .setTitle(target.getUser().getName())
                .setThumbnail(target.getUser().getAvatarUrl())
                .setDescription("Permissões de `" + target.getEffectiveName() + "`.")
                .addField("🔒 Permissões (" + permissions.size() + ")", permissions(permissions), false)
                .setColor(color)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private String permissions(EnumSet<Permission> perms) {
        StringBuilder builder = new StringBuilder().append("```\n");
        List<Permission> permissions = perms.stream().toList();


        if (permissions.isEmpty())
            return "`Nenhuma`";

        for (int i = 0; i < permissions.size(); i++) {
            if (i != 0) builder.append(", ");

            builder.append(permissions.get(i).getName());
        }

        return builder.append(".\n```").toString();
    }
}