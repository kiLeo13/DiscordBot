package bot.commands;

import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class RefreshStaffs extends BotCommand {

    public RefreshStaffs(String... name) {
        super(false, 0, Permission.MANAGE_SERVER, null, name);
    }

    @Override
    public void run(Message message, String[] args) {

        
        
    }
}