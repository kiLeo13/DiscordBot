package bot.util;

import bot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bot {
    private Bot() {}

    public static void tempMessage(MessageChannel channel, String message, int time) {
        if (channel == null) return;
        channel.sendMessage(message)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                );
    }

    public static void tempEmbed(MessageChannel channel, MessageEmbed embed, int time) {
        if (channel == null) return;
        channel.sendMessageEmbeds(embed)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                );
    }

    public static void tempReply(Message message, String content, int time) {
        message.reply(content)
                .delay(time, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void delete(Message message) {
        message.delete()
                .queue(null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static Role getRole(Guild guild, String id) {
        if (id == null) return null;
        id = id.replaceAll("[^0-9]+", "");

        if (id.isBlank()) return null;

        return guild.getRoleById(id);
    }

    public static Task<List<Member>> fetchMembers(Guild guild, String... ids) {
        try {
            final List<Long> inputs = Arrays.stream(ids)
                    .map(s -> Long.parseLong(s.replaceAll("[^0-9]+", "")))
                    .toList();

            return guild.retrieveMembersByIds(inputs);
        } catch (NumberFormatException e) {
            return guild.retrieveMembersByIds("-1");
        }
    }

    public static CacheRestAction<User> fetchUser(@NotNull String arg) {
        final JDA jda = Main.getApi();

        arg = arg.replaceAll("[^0-9]+", "");
        if (arg.isBlank()) arg = "-1";

        return jda.retrieveUserById(arg);
    }

    public static String parsePeriod(long period) {
        final StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(period);

        if (duration.toSeconds() == 0) return "0s";

        // It's safe to cast here as timeouts cannot exceed 2,419,200 seconds (28 days)
        int day = (int) duration.toDaysPart();
        int hrs = duration.toHoursPart();
        int min = duration.toMinutesPart();
        int sec = duration.toSecondsPart();

        if (day != 0) builder.append(String.format("%sd, ", day < 10 ? "0" + day : day));
        if (hrs != 0) builder.append(String.format("%sh, ", hrs < 10 ? "0" + hrs : hrs));
        if (min != 0) builder.append(String.format("%sm, ", min < 10 ? "0" + min : min));
        if (sec != 0) builder.append(String.format("%ss, ", sec < 10 ? "0" + sec : sec));

        String result = builder.toString().stripTrailing();
        return result.substring(0, result.length() - 1);
    }

    public static CacheRestAction<Member> fetchMember(Guild guild, String arg) {
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.isBlank()) arg = "-1";

        return guild.retrieveMemberById(arg);
    }

    public static void log(String str) {
        final HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{RESET}", "\033[0m");
        placeholders.put("{BLACK}", "\033[0;30m");
        placeholders.put("{RED}", "\033[0;31m");
        placeholders.put("{GREEN}", "\033[0;32m");
        placeholders.put("{YELLOW}", "\033[0;33m");
        placeholders.put("{BLUE}", "\033[0;34m");
        placeholders.put("{PURPLE}", "\033[0;35m");
        placeholders.put("{CYAN}", "\033[0;36m");
        placeholders.put("{WHITE}", "\033[0;37m");

        for (String k : placeholders.keySet())
            str = str.replace(k, placeholders.get(k));

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
            return String.join(System.lineSeparator(), Files.readAllLines(Path.of(file.getPath())));
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