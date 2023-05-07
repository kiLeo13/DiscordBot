package bot.commands;

import bot.util.CommandExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class PrivilegedHelp implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER )) return;

        channel.sendMessage("""
                ```
                
                ```
                """).queue();

        message.delete().queue();
    }
}