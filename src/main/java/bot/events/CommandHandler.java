package bot.events;

import bot.commands.Registration;
import bot.util.CommandExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {
    private static final HashMap<String, CommandExecutor> commands = new HashMap<>();
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

        if (event.getGuild().getIdLong() == 11111L) return;
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith(PREFIX) && !content.startsWith(PREFIX_REGISTER)) return;

        // Run command
        runCommand(message);
    }

    public void runCommand(Message message) {
        if (commands.isEmpty()) return;

        CommandExecutor registration = Registration.getInstance();
        String input = message.getContentRaw();
        String cmd = input.split(" ")[0];

        // Is a registration command?
        if (cmd.startsWith("r!") && !commands.containsKey(cmd))
            registration.run(message);

        CommandExecutor command = commands.get(cmd);
        if (command == null) return;

        command.run(message);
    }

    public void addListenerCommand(String name, CommandExecutor command) {
        final HashMap<String, String> prefixes = new HashMap<>();

        prefixes.put("<prefix>", PREFIX);
        prefixes.put("<register>", PREFIX_REGISTER);

        for (String i : prefixes.keySet())
            name = name.replaceAll(i, prefixes.get(i));

        if (name.stripTrailing().equals(""))
            throw new IllegalArgumentException("Command name cannot be empty");

        commands.put(name, command);
    }
}