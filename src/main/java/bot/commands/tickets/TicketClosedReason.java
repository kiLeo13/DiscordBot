package bot.commands.tickets;

import bot.util.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TicketClosedReason extends ListenerAdapter {
    private static final TicketStorage manager = TicketStorage.create();
    protected static final HashMap<MessageChannel, String> channelDeletionReason = new HashMap<>();

    @SubscribeEvent
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (!event.getInteraction().getModalId().equals("ticket-closed-reason")) return;

        TextChannel channel = event.getChannel().asTextChannel();
        String reason = event.getInteraction().getValue("reason").getAsString();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        TicketStorage.Ticket ticket = manager.getTicket(channel.getId());

        Bot.fetchUser(channelDeletionReason.get(channel)).queue(u -> {
            u.openPrivateChannel().queue(c -> {
                MessageEmbed embed = embed(member, reason);
                MessageCreateBuilder send = new MessageCreateBuilder();

                send.setEmbeds(embed);
                send.setContent(String.format("""
                        Olá, %s! Venho informar que o seu ticket: `%s` foi recusado.
                        """,
                        u.getName(),
                        ticket.subject().replace("`", "")
                ));

                c.sendMessage(send.build()).queue();
            });
        });

        CloseTicket.closeTicket(guild, channel, true, reason);
        channelDeletionReason.remove(channel);

        event.reply("O ticket será fechado em 10segundos...").setEphemeral(true).queue();
        channel.delete().queueAfter(10000, TimeUnit.MILLISECONDS);
    }

    private MessageEmbed embed(Member staff, String reason) {
        Guild guild = staff.getGuild();
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setAuthor(staff.getUser().getName(), null, staff.getUser().getAvatarUrl())
                .setColor(Color.RED)
                .addField("🎫 Motivo", reason.replace("`", ""), false)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }
}