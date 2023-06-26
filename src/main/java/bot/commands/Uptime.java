package bot.commands;

import bot.Main;
import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Uptime extends BotCommand {
    private static final byte timeZone = -3;

    public Uptime(String name) {
        super(true, 0, Permission.MESSAGE_MANAGE, null, name);
    }

    @Override
    public void run(Message message, String[] args) {

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        String response = formatted("""
                🕒** | Uptime:** `<uptime>`
                ⏰** | Desde:** `<init-date> às <init-time> (GMT -3)`
                """);

        channel.sendMessage("⌛｜<@" + author.getId() + "> **Bot Uptime:**\n\n" + response).queue();
    }

    private String formatted(String str) {
        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("<uptime>", getFormattedUptime());
        placeholders.put("<init-date>", getReadyMoment(true));
        placeholders.put("<init-time>", getReadyMoment(false));

        for (String p : placeholders.keySet())
            str = str.replaceAll(p, placeholders.get(p));

        return str;
    }

    private String getFormattedUptime() {
        long now = System.currentTimeMillis() / 1000;
        long init = Main.getInitTime() / 1000;
        long uptime = now - init;

        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(uptime, 0, ZoneOffset.UTC);

        DateTimeFormatter hoursFormatter = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter minutesFormatter = DateTimeFormatter.ofPattern("mm");
        DateTimeFormatter secondsFormatter = DateTimeFormatter.ofPattern("ss");

        int realDayUptime = (int) Math.floor(uptime / 86400.0);
        String days = String.valueOf(realDayUptime);
        String hours = hoursFormatter.format(dateTime);
        String minutes = minutesFormatter.format(dateTime);
        String seconds = secondsFormatter.format(dateTime);

        if (dateTime.getHour() < 10) days = "0" + days;

        String daysSuffix = " dia";
        String hoursSuffix = " hora";
        String minutesSuffix = " minuto";
        String secondsSuffix = " segundo";

        if (realDayUptime != 1) daysSuffix += "s";
        if (dateTime.getHour() != 1) hoursSuffix += "s";
        if (dateTime.getMinute() != 1) minutesSuffix += "s";
        if (dateTime.getSecond() != 1) secondsSuffix += "s";

        return days + daysSuffix + ", " +
                hours + hoursSuffix + ", " +
                minutes + minutesSuffix + " e " +
                seconds + secondsSuffix;
    }

    private String getReadyMoment(boolean returnDate) {
        long init = Main.getInitTime() / 1000;
        LocalDateTime time = LocalDateTime.ofEpochSecond(init, 0, ZoneOffset.UTC).plusHours(timeZone);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        /* 01/01/2000 às 13:45:34 */
        return returnDate
                ? dateFormatter.format(time)
                : timeFormatter.format(time);
    }
}