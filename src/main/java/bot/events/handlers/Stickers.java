package bot.events.handlers;

import bot.util.Images;
import bot.util.Requirements;
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

        long lapada = 1076684318115643483L;
        long tapa = 873931795010289664L;

        stickerMap.put(lapada, Images.TAPA.get()); // Lapada -> Tapa
        stickerMap.put(tapa, Images.LAPADA.get()); // Tapa -> Lapada

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        boolean hasSticker = !message.getStickers().isEmpty();
        long stickerId;

        if (!Requirements.HANDLER_STICKERS_CHANNELS.get().contains(channel.getIdLong())) return;

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