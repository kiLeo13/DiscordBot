package bot.commands;

import bot.Main;
import bot.util.Bot;
import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Shutdown implements SlashExecutor {

    @Override
    public void process(SlashCommandInteractionEvent event) {

        JDA api = Main.getApi();
        User user = event.getUser();
        event.reply("*Attempting to shut system down in 5 seconds...*").setEphemeral(true).queue();
        api.getPresence().setPresence(OnlineStatus.OFFLINE, false);

        Bot.setTimeout(() -> {
            api.shutdownNow();
            System.out.printf("""
                    ==============================
                    
                    ⚠ Bot has gone offline!
                    Requested by: [ %s ]
                    
                    ==============================
                    
                    """, user.getAsTag());
        }, 5000);
    }
}