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
import java.util.concurrent.TimeUnit;

public class PayServer extends Thread {
    private final User myuu;
    private static boolean hasInformed;
    private int day;

    public PayServer(JDA api) {
        myuu = api.getUserById(183645448509194240L);
        if (myuu == null) throw new IllegalArgumentException("Myuu cannot be null");
    }

    @Override
    public void run() {
        while (true) {
            updateDay();

            if (day == 20 && !hasInformed) {
                inform(myuu, """
                        Hoje é dia `<date>`
                        
                        Por favor, pague a bosta do servidor para que eu não morra.
                        
                        Link: <<link>>
                        """);
                hasInformed = true;
            } else
                hasInformed = false;

            try { Thread.sleep(3600000); }
            catch (InterruptedException ignore) {}
        }
    }

    private void updateDay() {
        day = LocalDateTime.now().getDayOfMonth();
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
                .delay(5, TimeUnit.DAYS)
                .flatMap(Message::delete)
                .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    private MessageEmbed getReminderEmbed(String content) throws IllegalAccessException {
        EmbedBuilder builder = new EmbedBuilder();
        Guild oficina = Main.getApi().getGuildById(582430782577049600L);

        if (oficina == null) throw new IllegalAccessException();

        builder.setTitle("Pagar Servidor DO BOT")
                .setColor(Color.PINK)
                .setThumbnail(Main.getApi().getSelfUser().getAvatarUrl())
                .addField("> Assunto", content, false)
                .setImage(oficina.getBannerUrl())
                .setFooter("Oficina Myuu", oficina.getIconUrl());

        return builder.build();
    }
}