package bot.events;

import bot.util.Channels;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public class Stickers extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final HashMap<Long, File> stickerMap = new HashMap<>();

        long lapada = 1076684318115643483L;
        long tapa = 873931795010289664L;

        stickerMap.put(lapada, new File("src/main/resources/content/images/tapa.png")); // Lapada -> Tapa
        stickerMap.put(tapa, new File("src/main/resources/content/images/lapada.png")); // Tapa -> Lapada

        Message message = event.getMessage();
        User author = event.getAuthor();
        MessageChannelUnion channel = event.getChannel();
        boolean hasSticker = !message.getStickers().isEmpty();
        long stickerId;

        if (!Channels.STICKERS_CHANNELS.get().contains(channel.getIdLong())) return;

        if (!hasSticker) return;
        else stickerId = message.getStickers().get(0).getIdLong();

        if (author.isBot()) return;
        if (!stickerMap.containsKey(stickerId)) return;

        message.replyFiles(FileUpload
                .fromData(stickerMap
                        .get(stickerId)))
                .queue();
    }
}