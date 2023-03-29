package bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.util.HashMap;

public class BigoAnnouncement {
    private BigoAnnouncement() {}

    protected static void help(Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Bigo Announcement", guild.getIconUrl())
                .setDescription("Segue uma explicação mais detalhada sobre o comando fornecido.")
                .addField("> 📝 Requisitos", "Para executar este comando, requer `Permission.MESSAGE_MANAGE` (em algum cargo, chat não serve) ou ser o Bigo.", true)
                .addField("> ❓ O que é", "Desenvolvido para divulgar a live do Bigo na Twitch *(comando sujeito à remoção em caso de baixa frequência de usos)*.", true)
                .addField("> ❗ Disclaimer", "Este comando NÃO É feito com a intenção de marcar everyone quando utilizado.", true)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public static void run(Message message) {
        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String announcement = getAnnouncement("""
                Vão lá conferir a live do <streamer>!
                
                <link>
                """);

        if (author.isBot() || member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE) && member.getIdLong() != 974159685764649010L) return;

        message.delete().queue();
        channel.sendMessage(announcement).queue();
    }

    private static String getAnnouncement(String str) {
        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("<streamer>", "Bigo");
        placeholders.put("<link>", "https://www.twitch.tv/poderosobigo");

        for (String p : placeholders.keySet())
            str = str.replaceAll(p, placeholders.get(p));

        return str;
    }
}