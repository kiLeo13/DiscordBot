package bot.commands.tickets;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.io.IOException;

public class MessageInputTicket extends ListenerAdapter {
    private static final TicketStorage manager = TicketStorage.create();

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();

        if (!manager.isFromTicket(channel.getId())) return;

        try {
            manager.storeTemporary(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}