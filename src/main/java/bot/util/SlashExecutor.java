package bot.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashExecutor {
    void run(SlashCommandInteractionEvent event);
}