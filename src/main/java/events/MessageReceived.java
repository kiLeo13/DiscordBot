package events;

import com.sun.tools.javac.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        pingComand(event);

    }

    private static void pingComand(MessageReceivedEvent e) {

        String message = e.getMessage().getContentRaw();
        User author = e.getAuthor();
        MessageChannelUnion channel = e.getChannel();
        JDA api = // What

        if (!message.equalsIgnoreCase("!ping")) return;
        if (author.isBot()) return;

        channel.sendMessage("Oioioioioioi, ping: `" + ).queue();
    }
}