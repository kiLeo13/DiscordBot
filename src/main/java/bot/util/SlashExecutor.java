package bot.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashExecutor {
    final static boolean ISPUBLIC = true;

    void runSlash(SlashCommandInteractionEvent event);
}