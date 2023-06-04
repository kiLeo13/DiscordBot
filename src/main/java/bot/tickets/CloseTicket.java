package bot.tickets;

import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CloseTicket implements SlashExecutor {
    private static final TicketStorage manager = TicketStorage.create();

    @Override
    public void process(SlashCommandInteractionEvent event) {


    }

    private void closeTicket() {
    }
}