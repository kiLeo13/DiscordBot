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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

    public static void setTimeout(Runnable runnable, long delay) {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        };

        timer.schedule(task, delay);
    }

    public static TimerTask setInterval(Runnable runnable, long delay) {
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                runnable.run();
            }
        };

        timer.scheduleAtFixedRate(task, 0, delay);
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

    public static String request(String path) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url(path)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful()
                ? response.body().string()
                : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static InputStream requestFile(String path) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url(path)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                return null;
            
            byte[] bytes = response.body().bytes();

            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static void log(String str) {
        LocalDateTime now = LocalDateTime.now();

        String hour = now.getHour() < 10 ? "0" + now.getHour() : String.valueOf(now.getHour());
        String minute = now.getMinute() < 10 ? "0" + now.getMinute() : String.valueOf(now.getMinute());
        String second = now.getSecond() < 10 ? "0" + now.getSecond() : String.valueOf(now.getSecond());

        System.out.printf("[%s:%s:%s]: %s\n", hour, minute, second, str);
    }

    public static Color hexToRgb(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            int a = Integer.parseInt(hex.substring(6, 8), 16);

            return new Color(r, g, b, a);
        } catch (NumberFormatException ignore) {}

        return null;
    }

    public static String reverse(String str) {
        StringBuilder builder = new StringBuilder();
        int begin = str.length() - 1;

        for (int i = begin; i >= 0; i--)
            builder.append(str.charAt(i));

        return builder.toString();
    }
}