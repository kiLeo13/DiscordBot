package bot.commands;

import java.util.List;

import bot.util.Tools;
import bot.util.CommandExecutor;
import bot.util.Roles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Linff implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        Member member = message.getMember();
        Member linff = Tools.findMember(guild, "577787431340736533");
        Role salada = guild.getRoleById(Roles.ROLE_SALADA.toId());
        List<String> swearings = List.of(
            "Filho da puta burro", "VIADINHO",
            "BICHA", "PAU NO CU", "Seu bosta");

        if (salada == null || member == null || !member.getRoles().contains(salada)) return;

        if (linff == null) {
            Tools.sendGhostMessage(channel, "Linff não foi encontrado.", 5000);
            message.delete().queue();
            return;
        }

        int random = (int) Math.floor(Math.random() * swearings.size());

        channel.sendMessage(String.format("<@%d> %s",
            linff.getIdLong(),
            swearings.get(random))).queue();
        
        message.delete().queue();
    }
}