package bot.commands;

import bot.util.CommandExecutor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

// This is just to learn how to use the SQL lib, come on
public class SQL implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();

        if (member == null || member.getIdLong() != 596939790532739075L) return;
    }   
}