package bot.generic_listeners;

import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SlashHandler extends ListenerAdapter {
    private static SlashHandler INSTANCE;
    private static final HashMap<String, SlashExecutor> commands = new HashMap<>();

    private SlashHandler() {}

    public static SlashHandler getManager() {
        if (INSTANCE == null) INSTANCE = new SlashHandler();
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final Member member = event.getMember();

        // Yeah, it HAS to be run in a guild
        if (event.getGuild() == null || member == null || event.getUser().isBot()) return;

        CommandHandler.EXECUTOR.execute(() -> runCommand(event));
    }

    public void runCommand(SlashCommandInteractionEvent event) {
        String cmd = event.getName().split(" ")[0];

        SlashExecutor command = commands.get(cmd);
        if (command == null) return;

        command.process(event);
    }

    public SlashHandler register(String name, SlashExecutor command) {

        if (name.stripTrailing().equals(""))
            throw new IllegalArgumentException("Command name cannot be empty");

        commands.put(name, command);
        return this;
    }
}