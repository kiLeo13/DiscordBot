package bot.commands;

import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.entities.Message;

@CommandPermission()
public class Help implements CommandExecutor {

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("Vish, não fiz o `.help` até hoje KKKKKKKKKKKKKKKKKKKK").queue();
    }
}