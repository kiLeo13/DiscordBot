package bot.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashExecutor extends CommandHelper {

    void process(SlashCommandInteractionEvent event);
}