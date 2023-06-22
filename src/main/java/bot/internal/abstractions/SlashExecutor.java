package bot.internal.abstractions;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

public interface SlashExecutor {
    void process(@NotNull SlashCommandInteraction event);
}