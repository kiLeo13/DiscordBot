package bot.util;

import bot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Bot {
    private static final Timer timer = new Timer(false);

    private Bot() {}

    /**
     * 
     * @param channel The channel to be sent the message
     * @param message The message to be sent in the channel
     * @param time The time (in milliseconds) the bot will wait before deleting the message
     * 
    **/
    public static void sendGhostMessage(MessageChannelUnion channel, String message, int time) {
        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    /**
     *
     * @param channel The channel to be sent the message
     * @param message The message to be sent in the channel
     * @param time The time (in milliseconds) the bot will wait before deleting the message
     *
     **/
    public static void sendGhostMessage(TextChannel channel, String message, int time) {
        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    /**
     *
     * @param message The message to be replied to
     * @param content The message to be sent in the channel
     * @param time The time (in milliseconds) the bot will wait before deleting the message
     *
     **/
    public static void sendGhostReply(Message message, String content, int time) {
        message.reply(content)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    /**
     *
     * @param message The message to be deleted
     * @param time The time (in milliseconds) the bot will wait before deleting the message
     *
     **/
    public static void deleteAfter(Message message, int time) {
        message.delete()
                .queueAfter(time, TimeUnit.MILLISECONDS,
                        null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    /**
     *
     * Useful for deleting a message you are not sure if still exists without throwing an exception
     *
     * @param message The message to be deleted
     *
     **/
    public static void delete(Message message) {
        message.delete()
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static TimerTask setTimeout(Runnable runnable, long delay) {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        };

        timer.schedule(task, delay);
        return task;
    }

    /**
     *
     * Looks for any member with the provided snowflake (id) in the given {@link Guild}
     *
     * @param guild The guild to search the member
     * @param arg The id or the argument of a command, if you provide <@123> it will only care about the id (the numbers provided)
     *
     * @return {@link Member} if one is found, null otherwise
     *
     **/
    public static Member findMember(Guild guild, String arg) {
        if (guild == null || arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.stripTrailing().equals("")) return null;

        try {
            return guild.retrieveMemberById(arg).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    /**
     *
     * Looks for any user with the provided snowflake (id)
     *
     * @param arg The id or the argument of a command, if you provide <@123> it will only care about the id (the numbers provided)
     *
     * @return {@link User} if one is found, null otherwise
     *
     **/
    public static User findUser(String arg) {
        if (arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.stripTrailing().equals("")) return null;

        try {
            return Main.getApi().retrieveUserById(arg).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }
}