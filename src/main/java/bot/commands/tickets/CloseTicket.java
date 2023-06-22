package bot.commands.tickets;

import bot.util.Bot;
import bot.util.content.Categories;
import bot.util.content.Channels;
import bot.internal.abstractions.SlashExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CloseTicket implements SlashExecutor {
    private static final TicketStorage manager = TicketStorage.create();

    @Override
    public void process(@NotNull SlashCommandInteraction event) {

        OptionMapping refused = event.getOption("refused");
        boolean isRefused = refused != null && refused.getAsBoolean();
        TextChannel channel = event.getChannel().asTextChannel();
        Category category = channel.getParentCategory();
        Guild guild = event.getGuild();

        if (!category.getId().equals(Categories.SUPPORT.id())) {
            event.reply("Esta categoria não é feita para tickets e por isso não pode ser fechada.").setEphemeral(true).queue();
            return;
        }

        if (!manager.isFromTicket(channel.getId())) {
            event.reply("Este canal não foi criado a partir de um ticket e não pode ser apagado por este comando.").setEphemeral(true).queue();
            return;
        }

        if (!manager.isTicketOpen(channel.getId())) {
            event.reply("Este ticket já foi fechado ou nunca existiu.").setEphemeral(true).queue();
            return;
        }

        // If the ticket was closed due to someone refused to respond
        if (isRefused) {
            TextInput reason = TextInput.create("reason", "Motivo", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Motivo de recusa do ticket")
                    .setRequiredRange(10, 1000)
                    .build();

            Modal modal = Modal.create("ticket-closed-reason", "Motivo de Recusa")
                    .addComponents(ActionRow.of(reason))
                    .build();

            event.replyModal(modal).queue();
            TicketClosedReason.channelDeletionReason.put(channel, manager.getTicket(channel.getId()).issuer());
        } else {
            event.reply("O ticket será fechado em 5 segundos...").setEphemeral(true).queue();
            channel.delete().queueAfter(5000, TimeUnit.MILLISECONDS);
            closeTicket(guild, channel, false, null);
        }
    }

    protected static void closeTicket(Guild guild, TextChannel channel, boolean refused, String reason) {
        File closedTicketContent = manager.getTemporary(channel.getId());
        TicketStorage.Ticket ticket = manager.getTicket(channel.getId());
        MessageChannel ticketLog = guild.getTextChannelById(Channels.LOG_CLOSED_TICKETS.id());
        MessageCreateBuilder send = new MessageCreateBuilder();
        LocalDateTime ticketCreation = LocalDateTime.ofEpochSecond(ticket.creation(), 0, ZoneOffset.UTC);

        if (ticketLog == null) {
            Bot.log("Could not find log for closed tickets.", true);
            return;
        }

        channel.getManager()
                .putMemberPermissionOverride(Long.parseLong(ticket.issuer()), null, List.of(Permission.MESSAGE_SEND))
                .queue();

        manager.setRefused(channel.getId(), refused, reason);

        // Sending the ticket-conversation to the channel
        send.setContent(String.format("""
                **Responsável:** <@%s>
                **Assunto:** `%s`
                **Criação:** <t:%s>
                **Recusado:** `%s`
                **Ticket ID:** `#%s`
                """,
                ticket.issuer(),
                ticket.subject(),
                ticketCreation.plusHours(3).toEpochSecond(ZoneOffset.UTC),
                ticket.refused().status() ? "Sim" : "Não",
                ticket.id()
                )
        );

        send.setFiles(FileUpload.fromData(
                closedTicketContent,
                String.format("%s_%s.txt",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ticketCreation),
                        ticket.issuer()
                )
        ));

        // Removing the temporary ticket message log file
        ticketLog.sendMessage(send.build()).queue(s -> closedTicketContent.delete());
    }
}