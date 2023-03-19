package bot.events;

import bot.commands.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String contentLC = message.getContentRaw().toLowerCase();

        switch (contentLC) {
            // case ".crole" -> ColorRoleSchedule.run(message);

            case ".disconnect" -> Disconnect.run(message);

            case ".ping" -> Ping.run(message);

            case ".bigo" -> BigoAnnouncement.run(message);

            case ".maconha", ".brisa" -> Maconha.run(message);

            case "r!roles" -> RegisterInputRoles.run(message);

            /* case "r!help" -> {
                RegisterHelp.run(message);
                return;
            }
             */
        }

        if (contentLC.startsWith(".moveall")) VoiceMoveAll.run(message);
        if (contentLC.startsWith(".puta")) Puta.run(message);
        if (contentLC.startsWith(".cd") || contentLC.startsWith(".countdown")) Countdown.run(message);
        if (contentLC.startsWith("r!take")) RegistrationTake.run(message);

        if (contentLC.startsWith("r!") && contentLC.split(" ").length >= 2) Registration.run(message);
    }
}