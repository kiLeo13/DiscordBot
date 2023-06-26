package bot.commands;

import bot.Main;
import bot.internal.abstractions.SlashExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Shutdown implements SlashExecutor {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void execute(SlashCommandInteraction event) {

        JDA api = Main.getApi();
        User user = event.getUser();
        event.reply("*Attempting to shut system down in 5 seconds...*").setEphemeral(true).queue();
        api.getPresence().setPresence(OnlineStatus.OFFLINE, false);

        scheduler.schedule(() -> {
            api.shutdownNow();
            System.out.printf("""
                    ==============================
                    
                    ⚠ Bot has gone offline!
                    Requested by: [ %s ]
                    
                    ==============================
                    
                    """, user.getName());
        }, 5000, TimeUnit.MILLISECONDS);
    }
}