package bot.util.schedules;

import bot.Main;
import bot.util.interfaces.BotScheduler;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PayServer implements BotScheduler {
    private static final List<Integer> hours = List.of(12, 15, 17, 20);

    @Override
    public void perform() {
        
        JDA api = Main.getApi();
        User user = api.retrieveUserById("183645448509194240").complete();
        LocalDateTime now = LocalDateTime.now();
        
        int day = now.getDayOfMonth();
        int hour = now.getHour();

        if (day != 20 || !hours.contains(hour))
            return;

        if (user == null)
            throw new IllegalArgumentException("User '183645448509194240' cannot be null for " + this.getClass().getName());

        String content = """        
                Hoje é dia `<date>`
                        
                Por favor, pague a bosta do servidor para que eu não morra.
                        
                Link: <<link>>
                """
                    .replaceAll("<date>", DateTimeFormatter.ofPattern("dd/MM").format(now))
                    .replaceAll("<link>", "https://hostsquare.com.br/");

        MessageEmbed embed = getEmbed(content, api.getGuildById("582430782577049600"));

        user.openPrivateChannel()
                .queue(dm -> dm.sendMessageEmbeds(embed)
                                .delay(2, TimeUnit.HOURS)
                                .flatMap(Message::delete)
                                .queue(),
                        e -> new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
    }

    private MessageEmbed getEmbed(String content, Guild guild) {
        EmbedBuilder builder = new EmbedBuilder();

        if (guild == null)
            throw new IllegalArgumentException("Oficina cannot be null");

        builder.setTitle("Pagar Servidor DO BOT")
                .setColor(Color.PINK)
                .setThumbnail(Main.getApi().getSelfUser().getAvatarUrl())
                .addField("> Assunto", content, false)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();

    }
}