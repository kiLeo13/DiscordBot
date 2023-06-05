package bot.tickets;

import bot.util.content.RegistrationRoles;
import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.time.Duration;
import java.util.HashMap;

public class OpenTicket implements SlashExecutor {
    protected static final HashMap<String, Long> cooldown = new HashMap<>();

    @Override
    public void process(SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();
        Role registered = guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.id());
        Cooldown waitTime = getCooldown(member);

        if (registered == null || !member.getRoles().contains(registered)) {
            event.reply("Apenas membros registrados podem abrir tickets.").setEphemeral(true).queue();
            return;
        }

        // At least 1 hour between two tickets
        if (waitTime.duration.getSeconds() != 0) {
            event.reply("Você precisa esperar `" + waitTime.format() + "` antes de abrir outro ticket..").setEphemeral(true).queue();
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

    protected Cooldown getCooldown(Member member) {
        long now = System.currentTimeMillis();

        if (!cooldown.containsKey(member.getId())) {
            cooldown.put(member.getId(), System.currentTimeMillis());
            return new Cooldown(Duration.ZERO);
        }

        long lastUse = cooldown.get(member.getId());

        // Return 0 if the cooldown is fine
        if (now - lastUse > 3600000)
            return new Cooldown(Duration.ZERO);

        return new Cooldown(Duration.ofMillis(3600000 - (now - lastUse)));
    }

    private record Cooldown(Duration duration) {

        public String format() {
            final StringBuilder builder = new StringBuilder();
            int secs = duration.toSecondsPart();
            int mins = duration.toMinutesPart();
            int hrs = duration.toHoursPart();

            if (hrs > 0) builder.append(String.format("%sh", hrs < 10 ? "0" + hrs : hrs)).append(", ");
            if (mins > 0) builder.append(String.format("%sm", mins < 10 ? "0" + mins : mins)).append(", ");
            builder.append(String.format("%ss", secs < 10 ? "0" + secs : secs)).append(", ");

            String converted = builder.toString().stripTrailing();
            return converted.substring(0, builder.length() - 2);
        }
    }
}