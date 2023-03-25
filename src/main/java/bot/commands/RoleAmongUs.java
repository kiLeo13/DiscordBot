package bot.commands;

import bot.util.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RoleAmongUs {
    private RoleAmongUs() {}

    public static void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        Member target;
        Role roleAmongUs = guild.getRoleById(Roles.ROLE_AMONG_US.toId());

        if (member == null || !isMemberAllowed(member)) return;
        if (Channels.STAFF_AJUDANTES_CHANNEL != channel.getIdLong()) return;

        if (roleAmongUs == null) {
            Extra.sendExpireMessage(channel,
                    Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.toMessage(),
                    10000);
            message.delete().queue();
            return;
        }

        try {
            String targetRegex = args[1].replaceAll("[^0-9]+", "");

            target = guild.retrieveMemberById(targetRegex).complete();
        } catch (ArrayIndexOutOfBoundsException e) {
            Extra.sendExpireMessage(channel,
                    Messages.ERROR_CHANNEL_NOT_FOUND.toMessage(),
                    5000);
            message.delete().queue();
            return;
        }

        if (target == null) {
            Extra.sendExpireMessage(channel,
                    Messages.ERROR_MEMBER_NOT_FOUND.toMessage(),
                    5000);
            message.delete().queue();
            return;
        }

        guild.addRoleToMember(target, roleAmongUs).queue();
        message.delete().queue();
        channel.sendMessage("<@" + member.getIdLong() + "> o cargo `" + roleAmongUs.getName() + "` foi adicionado com sucesso à <@" + target.getIdLong() + ">!").queue();
    }

    private static boolean isMemberAllowed(Member member) {
        List<Role> possibleRoles = new ArrayList<>();
        Guild guild = member.getGuild();
        AtomicBoolean returned = new AtomicBoolean(false);

        for (int i = 0; i < StaffField.GENERAL_AJUDANTES.toIds().size(); i++)
            if (i != StaffField.GENERAL_AJUDANTES.toIds().size()-2)
                possibleRoles.add(guild.getRoleById(StaffField.GENERAL_AJUDANTES.toIds().get(i)));

        possibleRoles.forEach(r -> {
            if (member.getRoles().contains(r))
                returned.set(true);
        });

        return returned.get();
    }
}