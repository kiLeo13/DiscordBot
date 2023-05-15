package bot.listeners;

import bot.commands.Registration;
import bot.data.BotData;
import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.MessageDeletion;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.EnumSet;
import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {
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

        if (!content.startsWith(BotData.PREFIX) && !content.startsWith(BotData.PREFIX_REGISTER)) return;

        // Run command
        runCommand(message);
    }

    public void runCommand(Message message) {
        if (commands.isEmpty()) return;

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
        Permission[] permissions = command.getClass().getAnnotation(CommandPermission.class).permissions();
        EnumSet<Permission> memberPermissions = member.getPermissions();

        if (permissions.length == 0) {
            command.run(message);
            return;
        }

        for (Permission p : permissions) {
            if (memberPermissions.contains(p)) {
                command.run(message);

                // Should it be automatically deleted?
                MessageDeletion annotation = command.getClass().getAnnotation(MessageDeletion.class);
                boolean deletion = annotation == null || annotation.value();

                if (deletion)
                    Bot.delete(message);
                break;
            }
        }
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
        return commands;
    }
}