package bot.commands;

import java.awt.Color;
import java.util.EnumSet;

import bot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@CommandPermission(permissions = Permission.ADMINISTRATOR)
public class Permissions implements CommandExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        Member target = args.length < 2 ? null : Bot.findMember(guild, args[1]);
        
        if (args.length < 2) {
            Bot.sendGhostMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            message.delete().queue();
            return;
        }

        if (target == null) {
            Bot.sendGhostMessage(channel, "Membro não encontrado. Caso esteja procurando por informações de um cargo, use `.roleinfo <role>.`", 10000);
            message.delete().queue();
            return;
        }

        MessageEmbed embed = embed(target);
        MessageCreateBuilder send = new MessageCreateBuilder();

        send.setContent("<@" + member.getIdLong() + ">");
        send.setEmbeds(embed);

        channel.sendMessage(send.build()).queue();
        message.delete().queue();
    }

    private MessageEmbed embed(Member target) {
        final EmbedBuilder builder = new EmbedBuilder();

        EnumSet<Permission> permissions = target.getPermissions();
        Guild guild = target.getGuild();
        Role highest = target.getRoles().get(0);
        Color color = highest == null ? null : highest.getColor();

        builder
                .setTitle(target.getUser().getAsTag())
                .setDescription("Permissões de `" + target.getEffectiveName() + "`.")
                .addField("🔒 Permissões (" + permissions.size() + ")", permissions(permissions), true)
                .setColor(color)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private String permissions(EnumSet<Permission> permissions) {
        StringBuilder builder = new StringBuilder().append("```\n");

        if (permissions.isEmpty())
            return "`Nenhuma`";

        for (int i = 0; i < permissions.size(); i++) {
            if (i != 0) builder.append(", ");

            builder.append(permissions.iterator().next().getName());
        }

        builder.append(".\n```");

        return builder.toString();
    }
}