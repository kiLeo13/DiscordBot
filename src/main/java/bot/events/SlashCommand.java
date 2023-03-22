package bot.events;

import bot.commands.Disconnect;
import bot.commands.DisconnectAll;
import bot.commands.Ping;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String name = event.getName();

        switch (name) {
            case "ping" -> Ping.run(event);
            case "disconnect" -> Disconnect.run(event);
            case "disconnectall" -> DisconnectAll.run(event);
        }
    }
}