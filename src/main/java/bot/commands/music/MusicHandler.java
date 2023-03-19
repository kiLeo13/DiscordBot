package bot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class MusicHandler extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getGuild().getIdLong() != 624008072544780309L) return;

        Message message = event.getMessage();
        String contentLC = message.getContentRaw().toLowerCase();

        if (contentLC.startsWith(".join")) CommandJoin.run(message);
    }
}