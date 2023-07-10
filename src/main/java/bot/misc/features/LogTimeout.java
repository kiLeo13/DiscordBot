package bot.misc.features;

import bot.util.Bot;
import bot.util.content.Channels;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.OffsetDateTime;

public class LogTimeout extends ListenerAdapter {

    @SubscribeEvent
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry entry = event.getEntry();
        AuditLogChange key = entry.getChangeByKey("communication_disabled_until");
        Guild guild = event.getGuild();

        if (entry.getType() != ActionType.MEMBER_UPDATE) return;

        // The triggered event was not a Member Update TIMEOUT
        if (key == null) return;

        long target = entry.getTargetIdLong();
        long author = entry.getUserIdLong();
        String oldInput = key.getOldValue();
        String currentInput = key.getNewValue();

        OffsetDateTime old = oldInput == null ? null : OffsetDateTime.parse(oldInput);
        OffsetDateTime current = currentInput == null ? null : OffsetDateTime.parse(currentInput);

        log(guild, target, author, old, current, entry.getReason());
    }

    private void log(Guild guild, long target, long author, OffsetDateTime old, OffsetDateTime current, String reason) {
        TextChannel log = guild.getTextChannelById(Channels.LOG_TIMEOUT.id());
        long unixTimeOld = old == null ? -1 : old.toEpochSecond();
        long unixTimeCurrent = current == null ? -1 : current.toEpochSecond();

        if (log == null) {
            Bot.log("{RED} timeout log channel was not found! Aborting process.");
            return;
        }

        guild.retrieveMemberById(target).queue(m -> {
            MessageEmbed embed = embed(guild, m.getUser(), target, author, unixTimeOld, unixTimeCurrent, reason);
            log.sendMessageEmbeds(embed).queue();
        }, e -> {
            MessageEmbed embed = embed(guild, null, target, author, unixTimeOld, unixTimeCurrent, reason);
            log.sendMessageEmbeds(embed).queue();
        });
    }

    private MessageEmbed embed(Guild guild, User target, long targetId, long admin, long old, long current, String reason) {
        final EmbedBuilder builder = new EmbedBuilder();
        boolean isPunishment = current != -1; // true means the timeout was added and NOT removed
        long now = System.currentTimeMillis() / 1000;
        String adminMention = String.format("<@%d>", admin);
        String targetMention = String.format("<@%d>", targetId);
        String title = String.format("%s %s!", target == null ? "Um membro" : target.getEffectiveName(), isPunishment ? "recebeu timeout" : "teve o timeout removido");
        String periodWhenAdded = String.format("<t:%d>\nRemove em: `%s`", current, Bot.parsePeriod(current - now));
        String previousPeriod = String.format("<t:%d>\nRestava: `%s`", old, Bot.parsePeriod(old - now));

        builder
                .setAuthor(title, null, target == null ? null : target.getAvatarUrl())
                .setColor(isPunishment ? Color.RED : Color.GREEN)
                .setFooter(guild.getName(), guild.getIconUrl());

        if (isPunishment) {
            builder
                    .addField("👑 Moderador", adminMention, true)
                    .addField("👥 Membro", targetMention, true)
                    .addField("📅 Término", periodWhenAdded, true)
                    .addField("📝 Motivo", reason == null ? "`Não especificado.`" : reason, true);
        } else {
            builder
                    .addField("👥 Membro", targetMention, true)
                    .addField("📅 Seria Removido", previousPeriod, true);
        }

        return builder.build();
    }
}