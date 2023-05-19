package bot.commands;

import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.entities.Message;

@CommandPermission()
public class Format implements CommandExecutor {

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("*This command has been disabled due to security issues.*\nThis command will be fully removed in: May 31, 2023.").queue();
    }
}