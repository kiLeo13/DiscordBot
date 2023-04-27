package bot.commands;

import java.util.List;

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
        String content = message.getContentRaw();
        Member member = message.getMember();
        Role salada = guild.getRoleById(Roles.ROLE_SALADA.toId());
        String[] args = content.split(" ");
        List<String> swearings = List.of(
            "Filho da puta burro", "VIADINHO",
            "BICHA", "PAU NO CU", "Seu bosta");

        if (salada == null || member == null || !member.getRoles().contains(salada)) return;
        int random = (int) Math.floor(Math.random() * swearings.size());

        if (args.length == 1) {
            channel.sendMessage(String.format("<@577787431340736533> %s", swearings.get(random))).queue();
        } else {
            channel.sendMessage("<@577787431340736533>" + content.substring(args[0].length())).queue();
        }
        
        message.delete().queue();
    }
}