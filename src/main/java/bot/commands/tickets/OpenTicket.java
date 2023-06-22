package bot.commands.tickets;

import bot.util.content.RegistrationRoles;
import bot.internal.abstractions.SlashExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;

public class OpenTicket implements SlashExecutor {
    protected static final HashMap<String, Long> cooldown = new HashMap<>();

    @Override
    public void process(@NotNull SlashCommandInteraction event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();
        Role registered = guild.getRoleById(RegistrationRoles.REGISTERED.id());
        long waitTime = member.hasPermission(Permission.MANAGE_SERVER) ? Long.MAX_VALUE : getSince(member);

        if (registered == null || !member.getRoles().contains(registered)) {
            event.reply("Apenas membros registrados podem abrir tickets.").setEphemeral(true).queue();
            return;
        }

        // At least 1 hour between two tickets
        if (waitTime < 3600000) {
            event.reply("Você precisa esperar `" + formatDuration(3600000 - waitTime) + "` antes de abrir outro ticket.").setEphemeral(true).queue();
            return;
        }

        TextInput subject = TextInput.create("subject", "Assunto", TextInputStyle.SHORT)
                .setPlaceholder("Assunto do ticket")
                .setRequiredRange(10, 32)
                .build();

        TextInput description = TextInput.create("description", "Descrição", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descreva aqui o seu problema")
                .setRequiredRange(20, 1000)
                .build();

        Modal modal = Modal.create("create-ticket", "Abra um Ticket")
                .addComponents(ActionRow.of(subject), ActionRow.of(description))
                .build();

        event.replyModal(modal).queue();
    }

    /**
     * Checks the cooldown of a member.
     *
     * <p>Note that to get how much time to run the command again you should do {@code time - getSince()};
     * <p>For example, if the time between runs is 1h, do {@code 3600000 - getSince()};
     *
     * @param member The member to check the cooldown.
     * @return How much time has passed since last time they used the command.
     */
    protected long getSince(Member member) {
        long now = System.currentTimeMillis();

        if (!cooldown.containsKey(member.getId())) {
            cooldown.put(member.getId(), System.currentTimeMillis());
            return Long.MAX_VALUE;
        }

        long lastUse = cooldown.get(member.getId());

        return now - lastUse;
    }

    private String formatDuration(long period) {
        final StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofMillis(period);
        int secs = duration.toSecondsPart();
        int mins = duration.toMinutesPart();
        int hour = duration.toHoursPart();

        if (hour != 0) builder.append(String.format("%sh", hour < 10 ? "0" + hour : hour)).append(", ");
        if (mins != 0) builder.append(String.format("%sm", mins < 10 ? "0" + mins : mins)).append(", ");
        builder.append(String.format("%ss", secs < 10 ? "0" + secs : secs));

        return builder.toString();
    }
}