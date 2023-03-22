package bot.commands;

import bot.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Uptime {
    private Uptime() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String response = formatted("""
                ⏱**｜Uptime:** `<uptime>`
                ⏰**｜Desde:** `<init-date> às <init-time>`
                """);

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        channel.sendMessage("⌛｜<@" + author.getIdLong() + "> **Bot Uptime:**\n\n" + response).queue();
        message.delete().queue();
    }

    private static String formatted(String str) {
        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("<uptime>", getUptime());
        placeholders.put("<init-date>", getReadyMoment(true));
        placeholders.put("<init-time>", getReadyMoment(false));

        for (String p : placeholders.keySet())
            str = str.replaceAll(p, placeholders.get(p));

        return str;
    }

    private static String getUptime() {
        long now = System.currentTimeMillis() / 1000;
        long init = Main.getInitTime() / 1000;
        long uptime = now - init;
        LocalDateTime time = LocalDateTime.ofEpochSecond(uptime, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return formatter.format(time) + "s";
    }

    private static String getReadyMoment(boolean returnDate) {
        long init = Main.getInitTime() / 1000;
        LocalDateTime time = LocalDateTime.ofEpochSecond(init, 0, ZoneOffset.UTC);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // 01/01/2000 às 13:45:34
        return returnDate
                ? dateFormatter.format(time)
                : timeFormatter.format(time);
    }
}