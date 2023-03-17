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

        // Countdown command
        if (contentLowerCase.startsWith(".cd") || contentLowerCase.startsWith(".countdown")) Countdown.run(message);

        // Disconnect command
        if (contentLowerCase.startsWith(".disconnect")) Disconnect.run(message);

        // Ping command
        if (contentLowerCase.startsWith(".ping")) Ping.run(message);

        // Register command
        if (contentLowerCase.startsWith("r!")) Registration.perform(message);

        // Swearing command
        if (contentLowerCase.startsWith(".puta")) Puta.run(message);
    }
}