package bot.commands;

import bot.util.annotations.CommandPermission;
import bot.util.interfaces.CommandExecutor;
import net.dv8tion.jda.api.entities.Message;

@CommandPermission()
public class Help implements CommandExecutor {

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("Vish, não fiz o `.help` até hoje KKKKKKKKKKKKKKKKKKKK").queue();
    }
}