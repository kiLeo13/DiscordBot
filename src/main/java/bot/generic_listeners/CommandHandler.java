package bot.generic_listeners;

import bot.commands.Registration;
import bot.data.BotData;
import bot.util.Bot;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import bot.util.interfaces.annotations.MessageDeletion;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler extends ListenerAdapter {
    protected static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Map<String, CommandExecutor> commands = new HashMap<>();
    private static CommandHandler INSTANCE;

    private CommandHandler() {}

    public static CommandHandler getManager() {
        if (INSTANCE == null) INSTANCE = new CommandHandler();
        return INSTANCE;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        Member member = message.getMember();
        String content = message.getContentRaw();

        if (member == null || member.getUser().isBot()) return;

        if (!content.startsWith(BotData.PREFIX) && !content.startsWith(BotData.PREFIX_REGISTER)) return;

        // Run command (on another thread)
        EXECUTOR.execute(() -> runCommand(message));
    }

    public void runCommand(Message message) {

        CommandExecutor registration = Registration.getInstance();
        Member member = message.getMember();
        String input = message.getContentRaw().toLowerCase();
        String cmd = input.split(" ")[0];

        // Is a registration command?
        if (cmd.startsWith("r!") && !commands.containsKey(cmd))
            registration.run(message);

        CommandExecutor command = commands.get(cmd);
        if (command == null) return;

        // This will check if they have the required permission
        final List<Permission> permissions = List.of(command.getClass().getAnnotation(CommandPermission.class).permissions());

        int i = 0;
        do {
            if (permissions.isEmpty() || member.hasPermission(permissions.get(i))) {
                command.run(message);

                MessageDeletion annotation = command.getClass().getAnnotation(MessageDeletion.class);
                boolean deletion = annotation == null || annotation.value();

                if (deletion)
                    Bot.delete(message);
                break;
            }

            i++;
        } while (i > permissions.size());
    }

    public CommandHandler register(String name, CommandExecutor command) {
        if (!command.getClass().isAnnotationPresent(CommandPermission.class))
            throw new IllegalArgumentException("Class " + command.getClass().getName() + " is not annotated with 'CommandPermission'");

        final HashMap<String, String> prefixes = new HashMap<>();

        prefixes.put("<prefix>", BotData.PREFIX);
        prefixes.put("<register>", BotData.PREFIX_REGISTER);

        for (String i : prefixes.keySet())
            name = name.replaceAll(i, prefixes.get(i)).toLowerCase();

        if (name.isBlank())
            throw new IllegalArgumentException("Command name cannot be empty");

        commands.put(name, command);
        return this;
    }

    public CommandHandler register(CommandExecutor command, String... name) {
        if (!command.getClass().isAnnotationPresent(CommandPermission.class))
            throw new IllegalArgumentException("Class " + command.getClass().getName() + " is not annotated with 'CommandPermission'");

        for (String n : name) {
            n = n.replaceAll("<prefix>", BotData.PREFIX).toLowerCase();

            if (n.isBlank()) throw new IllegalArgumentException("Command name cannot be empty");

            commands.put(n, command);
        }

        return this;
    }

    public static Map<String, CommandExecutor> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
}