package bot.util;

import bot.Main;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
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
    public static void tempMessage(MessageChannelUnion channel, String message, int time) {
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
    public static void tempMessage(TextChannel channel, String message, int time) {
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
    public static void tempReply(Message message, String content, int time) {
        message.reply(content)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    /**
     *
     * Useful for deleting a message you are not sure if still exists without throwing an exception.
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

    public static Member member(Guild guild, String arg) {
        if (guild == null || arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.stripTrailing().equals("")) return null;

        try {
            return guild.retrieveMemberById(arg).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public static User findUser(String arg) {
        if (arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.isBlank()) return null;

        try {
            return Main.getApi().retrieveUserById(arg).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public static void log(String str) {
        final HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("<RESET>", "\033[0m");
        placeholders.put("<BLACK>", "\033[0;30m");
        placeholders.put("<RED>", "\033[0;31m");
        placeholders.put("<GREEN>", "\033[0;32m");
        placeholders.put("<YELLOW>", "\033[0;33m");
        placeholders.put("<BLUE>", "\033[0;34m");
        placeholders.put("<PURPLE>", "\033[0;35m");
        placeholders.put("<CYAN>", "\033[0;36m");
        placeholders.put("<WHITE>", "\033[0;37m");

        for (String k : placeholders.keySet())
            str = str.replaceAll(k, placeholders.get(k));

        LocalDateTime now = LocalDateTime.now();

        String hour = now.getHour() < 10 ? "0" + now.getHour() : String.valueOf(now.getHour());
        String minute = now.getMinute() < 10 ? "0" + now.getMinute() : String.valueOf(now.getMinute());
        String second = now.getSecond() < 10 ? "0" + now.getSecond() : String.valueOf(now.getSecond());

        System.out.printf("[%s.%s.%s]: %s%s\n", hour, minute, second, str, "\033[0m");
    }

    public static String read(File file) {
        if (file == null)
            return "";

        try {
            return String.join("", Files.readAllLines(Path.of(file.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void write(String content, File file) {
        try (
                OutputStream out = Files.newOutputStream(Path.of(file.getPath()));
                Writer writer = new OutputStreamWriter(out);
                ) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}