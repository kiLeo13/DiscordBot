package bot.events;

import bot.events.handlers.AgeFilter;
import bot.events.handlers.Stickers;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class MessageReceivedGeneral extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        User author = event.getAuthor();

        if (!author.isBot()) triggerListeners(event);
    }

    private void triggerListeners(MessageReceivedEvent event) {

        Message message = event.getMessage();

        // Trigger age filter
        AgeFilter.perform(message);

        // Trigger stickers feature
        Stickers.run(message);
    }
}