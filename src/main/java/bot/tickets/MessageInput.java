package bot.tickets;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class MessageInput extends ListenerAdapter {
    private static final TicketStorage manager = TicketStorage.create();

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message message = event.getMessage();
        Member member = event.getMember();
        String content = message.getContentRaw();
        MessageChannelUnion channel = message.getChannel();
        Category category = channel.asTextChannel().getParentCategory();

        if (member == null || category == null) return;

        if (!manager.isFromTicket(channel)) return;

        if ()

    }
}