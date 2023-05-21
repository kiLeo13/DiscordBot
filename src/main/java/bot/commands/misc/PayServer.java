package bot.commands.misc;

import bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.util.Bot.setInterval;

public class PayServer {
    private int day;
    private int hour;
    private static final List<Integer> hours = List.of(12, 15, 17, 20);

    public PayServer(JDA api) {
        api.retrieveUserById("183645448509194240").queue(myuu -> setInterval(() -> {
            updateDate();

            if (day == 20 && hours.contains(hour)) {
                inform(myuu);
            }
        }, 3600 * 1000));
    }

    private void updateDate() {
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
    }

    private void inform(User user) {
        String content = """        
                Hoje é dia `<date>`
                        
                Por favor, pague a bosta do servidor para que eu não morra.
                        
                Link: <<link>>
                """;
        final HashMap<String, String> placeholders = new HashMap<>();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        placeholders.put("<date>", dateFormatter.format(date));
        placeholders.put("<link>", "https://hostsquare.com.br/");

        for (String i : placeholders.keySet())
            content = content.replaceAll(i, placeholders.get(i));

        MessageEmbed finalContent;

        try { finalContent = getReminderEmbed(content); }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(finalContent))
                .delay(3, TimeUnit.HOURS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE, ErrorResponse.CANNOT_SEND_TO_USER));
    }

    private MessageEmbed getReminderEmbed(String content) throws IllegalAccessException {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = Main.getApi().getGuildById(582430782577049600L);

        if (guild == null) throw new IllegalAccessException("Oficina cannot be null");

        builder.setTitle("Pagar Servidor DO BOT")
                .setColor(Color.PINK)
                .setThumbnail(Main.getApi().getSelfUser().getAvatarUrl())
                .addField("> Assunto", content, false)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }
}