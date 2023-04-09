package bot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.TimeUnit;

public class Bot {
    private Bot() {}

    public static void sendExpireMessage(MessageChannelUnion channel, String message, int time) {
        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void sendExpireMessage(MessageChannelUnion channel, String message, int time, Object... formats) {
        message = String.format(message, formats);

        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void sendExpireMessage(TextChannel channel, String message, int time) {
        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void sendExpireReply(Message message, String content, int time) {
        message.reply(content)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void deleteAfter(Message message, int time) {
        message.delete()
                .queueAfter(time, TimeUnit.MILLISECONDS,
                        null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void delete(Message message) {
        message.delete()
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void setTimeout(Runnable task, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}