package bot.commands;

import bot.Main;
import bot.util.Bot;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Shutdown implements SlashExecutor {

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        JDA api = Main.getApi();
        User user = event.getUser();
        event.reply("*Attempting to shut system down in 5 seconds...*").setEphemeral(true).queue();

        Bot.setTimeout(() -> {
            api.getPresence().setPresence(OnlineStatus.OFFLINE, false);
            api.shutdownNow();
            System.out.println("Bot has gone offline!\nRequested by: [" + user.getName() + "#" + user.getDiscriminator() + "]");
        }, 5000);
    }
}