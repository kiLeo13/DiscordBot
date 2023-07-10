package bot.commands;

import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.entities.Message;

public class Help extends BotCommand {

    public Help(String name) {
        super("{cmd} [command] [--hidden | --access]", name);
    }

    @Override
    public void run(Message message, String[] args) {

        // BRUH
        message.getChannel().sendMessage("Help está sendo desenvolvido. TENHAM CALMA.").queue();
    }
}