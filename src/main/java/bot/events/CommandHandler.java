package bot.events;

import bot.util.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommandHandler extends ListenerAdapter {
    private final HashMap<List<String>, Command> commands = new HashMap<>();
    private static CommandHandler INSTANCE;
    private static final String PREFIX = ".";
    private static final String PREFIX_REGISTER = "r!";

    private CommandHandler() {}

    public static CommandHandler getInstance() {
        if (INSTANCE == null) INSTANCE = new CommandHandler();
        return INSTANCE;
    }

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getGuild().getIdLong() != 582430782577049600L) return;
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        // Run command
        if (content.toLowerCase(Locale.ROOT).startsWith(PREFIX))
            runCommand(message);
    }

    private void runCommand(Message message) {
        String input = message.getContentRaw();
        String[] split = input.split(" ");
        String cmd = split[0];

        Command command = null;
        for (List<String> i : commands.keySet()) {
            if (!i.contains(cmd)) continue;

            command = commands.get(i);
            break;
        }

        if (command == null) return;

        command.run(message);
    }

    public void addListenerCommand(String name, Command command) {
        final HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{prefix}", PREFIX);
        placeholders.put("{r!-prefix}", PREFIX_REGISTER);

        for (String i : placeholders.keySet())
            name = name.replaceAll(i, placeholders.get(i));

        if (name.stripTrailing().equals(""))
            throw new IllegalArgumentException("Command name cannot be empty");

        commands.put(List.of(name), command);
    }
}