package bot.internal.abstractions;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public interface SlashExecutor {
    void execute(SlashCommandInteraction event);
}