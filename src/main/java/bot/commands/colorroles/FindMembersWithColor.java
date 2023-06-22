package bot.commands.colorroles;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Roles;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandPermission(permissions = Permission.MESSAGE_MANAGE)
public class FindMembersWithColor extends BotCommand {

    public FindMembersWithColor(String name) {
        super(name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {
        
        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        Role staffOficina = guild.getRoleById(Roles.ROLE_STAFF_OFICINA.id());

        if (staffOficina == null || !member.getRoles().contains(staffOficina)) return;

        if (args.length < 1) {

            return;
        }

        Role role = Bot.getRole(guild, args[0]);

        if (role != null) {
            final MessageCreateBuilder send = new MessageCreateBuilder();
            final EmbedBuilder builder = new EmbedBuilder();
            RoleIcon roleIcon = role.getIcon();

            guild.findMembersWithRoles(role).onSuccess(l -> {
                // Color
                builder.setColor(l.isEmpty() ? Color.RED : role.getColor())

                // Description
                        .setDescription(l.isEmpty() ? null : "Membros com o cargo " + role.getAsMention() + ".")

                // Thumbnail
                        .setThumbnail(l.isEmpty() || roleIcon == null ? null : roleIcon.getIconUrl())

                // Footer
                        .setFooter(guild.getName(), guild.getIconUrl());

                // Fields
                if (!l.isEmpty())
                    builder.addField("👥 Membros (" + l.size() + ")", format(l.stream().map(Member::getAsMention).toList()), true);

                send.setEmbeds(builder.build());
                send.setContent(member.getAsMention());

                channel.sendMessage(send.build()).queue();
            }).onError(t -> {
                t.printStackTrace();
                Bot.tempMessage(channel, "Algo deu errado. Verifique o console para mais informações sobre o erro.", 10000);
            });

            return;
        }

        Bot.fetchMember(guild, args[0]).queue(m -> {
            final ColorRoleData roles = getColorRoles(m);


        });
    }

    /**
     * Returns a {@link ColorRoleData} which contains the {@link Role} color, when it was added
     * and when it will be removed.
     *
     * @param member The member to look for the color roles.
     * @return A {@link ColorRoleData} object containing the desired data, null otherwise.
     */
    private ColorRoleData getColorRoles(@NotNull Member member) {
        Guild guild = member.getGuild();
        final Map<String, ColorRole.Colors> colors = new HashMap<>(ColorRole.convert(Bot.read(ColorRole.file)));
        List<Role> colorRoles = new ArrayList<>();
        long removal;
        long added;

        colors.forEach((role, data) -> {

        });

        return null;
    }

    private String format(List<String> values) {
        final StringBuilder builder = new StringBuilder();

        for (String s : values)
            builder.append(s).append("\n");

        return builder.toString().stripTrailing();
    }

    private record ColorRoleData(
            List<Role> roles,
            long removal,
            long added
    ) {}
}