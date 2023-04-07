package bot.commands;

import bot.util.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static bot.util.Bot.sendExpireMessage;

public class RoleAmongUs implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        Member target;
        Role roleAmongUs = guild.getRoleById(Roles.ROLE_AMONG_US.toId());

        if (member == null || !isMemberAllowed(member)) return;
        if (Channels.STAFF_AJUDANTES_CHANNEL.toId() != channel.getIdLong()) return;

        if (roleAmongUs == null) {
            Bot.sendExpireMessage(channel,
                    Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message(),
                    10000);
            message.delete().queue();
            return;
        }

        if (args.length < 2) {
            Bot.sendExpireMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 5000);
            message.delete().queue();
            return;
        }

        try {
            target = guild.retrieveMemberById(args[1].replaceAll("[^0-9]+", "")).complete();
        } catch (ErrorResponseException e) {
            sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
            message.delete().queue();
            return;
        }

        if (target.getRoles().contains(roleAmongUs)) {
            channel.sendMessage("<@" + member.getIdLong() + "> o membro <@" + target.getIdLong() + "> já tem o cargo `Já Participou (Among Us)`.").queue();
            message.delete().queue();
            return;
        }

        guild.addRoleToMember(target, roleAmongUs).queue();
        message.delete().queue();
        channel.sendMessage("<@" + member.getIdLong() + "> o cargo `Já Participou (Among Us)` foi adicionado com sucesso à <@" + target.getIdLong() + ">!").queue();
    }

    private static boolean isMemberAllowed(Member member) {
        List<Role> possibleRoles = new ArrayList<>();
        Guild guild = member.getGuild();
        AtomicBoolean returned = new AtomicBoolean(false);

        for (int i = 0; i < StaffRoles.GENERAL_AJUDANTES.toIds().size()-2; i++)
            possibleRoles.add(guild.getRoleById(StaffRoles.GENERAL_AJUDANTES.toIds().get(i)));

        possibleRoles.forEach(r -> {
            if (member.getRoles().contains(r))
                returned.set(true);
        });

        return returned.get();
    }
}