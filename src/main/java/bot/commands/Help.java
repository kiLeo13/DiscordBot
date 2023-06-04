package bot.commands;

import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.entities.Message;

@CommandPermission()
public class Help implements CommandExecutor {

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("Vish, não fiz o `.help` até hoje KKKKKKKKKKKKKKKKKKKK").queue();
    }
}