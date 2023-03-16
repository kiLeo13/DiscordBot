package bot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.TimeUnit;

public class Feature {

    private Feature() {}

    public static void sendBombMessage(MessageChannelUnion channel, String message, int time) {
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void replyBombMessage(Message message, String content, int time) {
        message.reply(content)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }
}