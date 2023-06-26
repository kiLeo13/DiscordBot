package bot.commands;

import bot.internal.abstractions.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ServerInfo extends BotCommand {

    public ServerInfo(String name) {
        super(true, null, name);
    }

    @Override
    public void run(Message message, String[] args) {

        MessageChannelUnion channel = message.getChannel();
        User author = message.getAuthor();
        Guild guild = message.getGuild();
        MessageCreateBuilder send = new MessageCreateBuilder();

        send.setContent(author.getAsMention());
        send.addEmbeds(embed(guild));

        channel.sendMessage(send.build()).queue();
    }

    private MessageEmbed embed(Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();

        String creation = creation(guild);
        long creationLong = guild.getTimeCreated().toEpochSecond();

        Member owner = guild.retrieveOwner().complete();
        String ownerName = owner == null ? "Not found" : owner.getEffectiveName();
        String banner = guild.getBannerUrl() == null ? "" : guild.getBannerUrl() + "?size=2048";

        List<GuildChannel> channels = guild.getChannels(true);
        List<GuildChannel> textChannels = channels.stream().filter(c -> c.getType().equals(ChannelType.TEXT)).toList();
        List<GuildChannel> audioChannels = channels.stream().filter(c -> c.getType().equals(ChannelType.VOICE)).toList();
        List<GuildChannel> categories = channels.stream().filter(c -> c.getType().equals(ChannelType.CATEGORY)).toList();
        List<ThreadChannel> threads = guild.retrieveActiveThreads().complete();

        builder
                .setTitle("<a:M_Myuu:643942157325041668> " + guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setColor(new Color(193, 126, 142))
                .addField("🌐 Server ID", "`" + guild.getOwnerIdLong() + "`", true)
                .addField("📅 Criação", creation + " (<t:" + creationLong + ":R>", true)
                .addField("👑 Dono", "`" + ownerName + "`", true)
                .addField("💬 Canais (e Categorias) (" + channels.size() + ")", String.format("""
                        📝 Texto: `%d`
                        🔉 Áudio: `%d`
                        ⚽ Categorias: `%d`
                        🎈 Threads: `%d`
                        """, textChannels.size(), audioChannels.size(), categories.size(), threads.size()), true)
                .setImage(banner)
                .setFooter(guild.getName(), guild.getIconUrl());

        builder.addField("👥 Membros (" + guild.getMemberCount() + ")", "", false);

        return builder.build();
    }

    private String creation(Guild guild) {
        String[] weekDays = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"};
        String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        // -3 for Brasília TimeZone
        LocalDateTime time = LocalDateTime.ofEpochSecond(guild.getTimeCreated().toEpochSecond(), 0, ZoneOffset.UTC).minusHours(3);

        int minute = time.getMinute();
        int hour = time.getHour();
        int day = time.getDayOfMonth();
        String week = weekDays[time.getDayOfWeek().getValue() - 1];
        String month = months[time.getMonth().getValue() - 1];
        int year = time.getYear();

        return String.format("%d de %s, %d (%s) às %d:%d", day, month, year, week, hour, minute);
    }
}