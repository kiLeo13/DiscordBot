package bot.util.interfaces;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public interface CommandExecutor extends CommandHelper {
    void run(@NotNull Message message);
}