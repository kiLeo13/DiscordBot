package bot.commands;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import bot.util.content.Roles;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandPermission()
public class RoleAmongUs implements CommandExecutor {

    @Override
    public void run(@NotNull Message message) {

        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        Role roleAmongUs = guild.getRoleById(Roles.ROLE_AMONG_US.id());

        if (!isMemberAllowed(member)) return;
        if (!Channels.STAFF_AJUDANTES_CHANNEL.id().equals(channel.getId())) return;

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 5000);
            return;
        }

        if (roleAmongUs == null) {
            Bot.tempMessage(channel, Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message(), 10000);
            return;
        }

        Bot.fetchMember(guild, args[1]).queue(m -> {
            if (m.getRoles().contains(roleAmongUs)) {
                channel.sendMessage("O membro <@" + m.getId() + "> já tem o cargo `" + roleAmongUs.getName() + "`.").queue();
                return;
            }

            if (m.getUser().isBot()) {
                Bot.tempMessage(channel, "Este comando não pode ser usado em bots.", 10000);
                return;
            }

            guild.addRoleToMember(m, roleAmongUs).queue();
            channel.sendMessage(String.format("<@%s> o cargo `%s` foi adicionado com sucesso à <@%s>!", member.getId(), roleAmongUs.getName(), m.getId())).queue();
        }, e -> Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000));
    }

    private boolean isMemberAllowed(Member member) {
        List<Role> possibleRoles = new ArrayList<>();
        Guild guild = member.getGuild();

        // Ajudantes Superior+
        for (int i = 0; i < Roles.GENERAL_AJUDANTES.ids().size() - 2; i++)
            possibleRoles.add(guild.getRoleById(Roles.GENERAL_AJUDANTES.ids().get(i)));

        for (Role r : possibleRoles)
            if (member.getRoles().contains(r))
                return true;

        return false;
    }
}