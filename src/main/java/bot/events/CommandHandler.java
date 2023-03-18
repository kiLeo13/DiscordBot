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
        String contentLowerCase = message.getContentRaw().toLowerCase();

        switch (contentLowerCase) {
            // case ".crole" -> ColorRoleSchedule.run(message);

            case ".cd", ".countdown" -> Countdown.run(message);

            case ".disconnect" -> Disconnect.run(message);

            case ".ping" -> Ping.run(message);

            case "r!roles" -> {
                RegisterInputRoles.run(message);
                return;
            }

            case "r!help" -> {
                RegisterHelp.run(message);
                return;
            }

            case ".puta" -> Puta.run(message);
        }

        if (contentLowerCase.startsWith("r!")) Registration.run(message);
    }
}