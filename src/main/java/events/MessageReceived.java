package events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceive(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        event.getGuildChannel().sendMessage("Olha a puta bosta que vc mandou: `" + message + "`").queue();
    }
}