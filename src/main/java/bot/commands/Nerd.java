package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Roles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Nerd implements CommandExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        Guild guild = message.getGuild();
        Role salada = guild.getRoleById(Roles.ROLE_SALADA.toId());
        Role alfea = guild.getRoleById(Roles.ROLE_ALFEA.toId());
        MessageChannelUnion channel = message.getChannel();

        if (salada == null || alfea == null) {
            System.out.println("Could not find role 'salada' or 'alfea'. Ignoring `nerd` command...");
            return;
        }

        if (member == null || !(member.getRoles().contains(salada) && member.getRoles().contains(alfea))) return;

        Bot.sendGhostMessage(channel, "*This command is still in development stages.*", 10000);
    }
}