package bot.misc.features;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class LogTimeout extends ListenerAdapter {

    @SubscribeEvent
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {

        AuditLogEntry entry = event.getEntry();
        AuditLogChange key = entry.getChangeByKey("communication_disabled_until");

        if (entry.getType() != ActionType.MEMBER_UPDATE) return;

        // The triggered event was not a timeout
        if (key == null) return;

        String target = entry.getTargetId();
        String author = entry.getUserId();
    }
}