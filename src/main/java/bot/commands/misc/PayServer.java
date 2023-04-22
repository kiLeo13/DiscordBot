package bot.commands.misc;

import bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PayServer extends Thread {
    private final User myuu;
    private int day;
    private int hour;
    private static final List<Integer> hours = List.of(12, 15, 17, 20);

    public PayServer(JDA api) {
        try {
            myuu = api.retrieveUserById(596939790532739075L).complete();
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            updateDate();

            if (day == 20 && hours.contains(hour)) {
                inform(myuu, """
                        Hoje é dia `<date>`
                        
                        Por favor, pague a bosta do servidor para que eu não morra.
                        
                        Link: <<link>>
                        """);
            }

            try { Thread.sleep(3600 * 1000); }
            catch (InterruptedException e) { break; }
        }
    }

    private void updateDate() {
        day = LocalDateTime.now().getDayOfMonth();
        hour = LocalDateTime.now().getHour();
    }

    private void inform(User user, String content) {
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
                .delay(5, TimeUnit.HOURS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    private MessageEmbed getReminderEmbed(String content) throws IllegalAccessException {
        EmbedBuilder builder = new EmbedBuilder();
        Guild oficina = Main.getApi().getGuildById(582430782577049600L);

        if (oficina == null) throw new IllegalAccessException("Oficina cannot be null");

        String banner = oficina.getBannerUrl() == null ? "" : oficina.getBannerUrl();

        if (!banner.equals(""))
            banner += "?size=2048";

        builder.setTitle("Pagar Servidor DO BOT")
                .setColor(Color.PINK)
                .setThumbnail(Main.getApi().getSelfUser().getAvatarUrl())
                .addField("> Assunto", content, false)
                .setImage(banner)
                .setFooter("Oficina Myuu", oficina.getIconUrl());

        return builder.build();
    }
}