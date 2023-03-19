package bot.events.handlers;

import bot.util.Channels;
import bot.util.Images;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.HashMap;

public class Stickers {
    private Stickers() {}

    public static void run(Message message) {
        final HashMap<Long, File> stickerMap = new HashMap<>();

        long lapada1 = 1076201232487686265L;
        long lapada = 1076684318115643483L;
        long tapa = 873931795010289664L;

        stickerMap.put(lapada, new File(Images.TAPA.getFile())); // Lapada -> Tapa
        stickerMap.put(lapada1, new File(Images.TAPA.getFile())); // Lapada -> Tapa
        stickerMap.put(tapa, new File(Images.LAPADA.getFile())); // Tapa -> Lapada

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        boolean hasSticker = !message.getStickers().isEmpty();
        long stickerId;

        if (!Channels.FEATURE_STICKERS_CHANNELS.contains(channel.getIdLong())) return;

        if (!hasSticker) return;
        else stickerId = message.getStickers().get(0).getIdLong();

        if (author.isBot()) return;
        if (!stickerMap.containsKey(stickerId) || stickerMap.get(stickerId) == null) return;

        message.replyFiles(FileUpload
                        .fromData(stickerMap
                                .get(stickerId)))
                .queue();
    }
}