package bot.commands;

import bot.util.*;
import bot.util.annotations.CommandPermission;
import bot.util.interfaces.CommandExecutor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.ArrayList;
import java.util.List;

@CommandPermission()
public class RoleAmongUs implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        Member target = args.length < 2 ? null : Bot.member(guild, args[1]);
        Role roleAmongUs = guild.getRoleById(Roles.ROLE_AMONG_US.id());

        if (!isMemberAllowed(member)) return;
        if (Channels.STAFF_AJUDANTES_CHANNEL.id() != channel.getIdLong()) return;

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 5000);
            return;
        }

        if (roleAmongUs == null) {
            Bot.tempMessage(channel, Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message(), 10000);
            return;
        }

        if (target == null) {
            Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000);
            return;
        }

        if (target.getUser().isBot()) {
            Bot.tempMessage(channel, "Um bot não pode receber este cargo.", 10000);
            return;
        }

        if (target.getRoles().contains(roleAmongUs)) {
            channel.sendMessage("O membro <@" + target.getIdLong() + "> já tem o cargo `Já Participou (Among Us)`.").queue();
            return;
        }

        guild.addRoleToMember(target, roleAmongUs).queue();
        channel.sendMessage("<@" + member.getIdLong() + "> o cargo `Já Participou (Among Us)` foi adicionado com sucesso à <@" + target.getIdLong() + ">!").queue();
    }

    private boolean isMemberAllowed(Member member) {
        List<Role> possibleRoles = new ArrayList<>();
        Guild guild = member.getGuild();

        for (int i = 0; i < Roles.GENERAL_AJUDANTES.ids().size() - 2; i++)
            possibleRoles.add(guild.getRoleById(Roles.GENERAL_AJUDANTES.ids().get(i)));

        for (Role r : possibleRoles)
            if (member.getRoles().contains(r))
                return true;

        return false;
    }
}