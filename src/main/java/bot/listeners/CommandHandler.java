package bot.listeners;

import bot.commands.Registration;
import bot.commands.lifetimemute.LifeMuteCommand;
import bot.data.BotData;
import bot.util.Bot;
import bot.util.annotations.CommandPermission;
import bot.util.annotations.MessageDeletion;
import bot.util.interfaces.CommandExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler extends ListenerAdapter {
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final HashMap<String, CommandExecutor> commands = new HashMap<>();
    private static CommandHandler INSTANCE;

    private CommandHandler() {}

    public static CommandHandler getInstance() {
        if (INSTANCE == null) INSTANCE = new CommandHandler();
        return INSTANCE;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        Member member = message.getMember();
        String content = message.getContentRaw();

        if (member == null || member.getUser().isBot()) return;

        // If user is life muted, fuck them
        if (LifeMuteCommand.isLifeMuted(member)) {
            Bot.delete(message);
            return;
        }

        if (!content.startsWith(BotData.PREFIX) && !content.startsWith(BotData.PREFIX_REGISTER)) return;

        // Run command (on another thread)
        executor.execute(() -> runCommand(message));
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
        Permission permission = command.getClass().getAnnotation(CommandPermission.class).permission();

        if (member.hasPermission(permission) || permission.getName().equals("UNKNOWN"))
            command.run(message);

        // Should it be automatically deleted?
        MessageDeletion annotation = command.getClass().getAnnotation(MessageDeletion.class);
        boolean deletion = annotation == null || annotation.value();

        if (deletion)
            Bot.delete(message);
    }

    public void addCommand(String name, CommandExecutor command) {
        if (!command.getClass().isAnnotationPresent(CommandPermission.class))
            throw new IllegalArgumentException("Class " + command.getClass().getName() + " is not annotated with 'CommandPermission'");

        final HashMap<String, String> prefixes = new HashMap<>();

        prefixes.put("<prefix>", BotData.PREFIX);
        prefixes.put("<register>", BotData.PREFIX_REGISTER);

        for (String i : prefixes.keySet())
            name = name.replaceAll(i, prefixes.get(i)).toLowerCase();

        if (name.stripTrailing().equals(""))
            throw new IllegalArgumentException("Command name cannot be empty");

        if (name.split(" ").length != 1)
            throw new IllegalArgumentException("Command name cannot contain multiple words");

        commands.put(name, command);
    }

    public void addCommand(CommandExecutor command, String... name) {
        if (!command.getClass().isAnnotationPresent(CommandPermission.class))
            throw new IllegalArgumentException("Class " + command.getClass().getName() + " is not annotated with 'CommandPermission'");

        for (String n : name) {
            n = n.replaceAll("<prefix>", BotData.PREFIX).toLowerCase();

            if (n.stripTrailing().equals("")) throw new IllegalArgumentException("Command name cannot be empty");
            if (n.split(" ").length != 1) throw new IllegalArgumentException("Command name cannot contain multiple words");

            commands.put(n, command);
        }
    }

    public static HashMap<String, CommandExecutor> getCommands() {
        return (HashMap<String, CommandExecutor>) Collections.unmodifiableMap(commands);
    }
}