package bot.listeners;

import bot.commands.Registration;
import bot.data.BotData;
import bot.util.CommandExecutor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

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

        if (event.getGuild().getIdLong() == 11111L) return;
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (!content.startsWith(BotData.PREFIX) && !content.startsWith(BotData.PREFIX_REGISTER)) return;

        // Run command
        runCommand(message);
    }

    public void runCommand(Message message) {
        if (commands.isEmpty()) return;

        CommandExecutor registration = Registration.getInstance();
        String input = message.getContentRaw().toLowerCase();
        String cmd = input.split(" ")[0];

        // Is a registration command?
        if (cmd.startsWith("r!") && !commands.containsKey(cmd))
            registration.run(message);

        CommandExecutor command = commands.get(cmd);
        if (command == null) return;

        command.run(message);
    }

    public void addCommand(String name, CommandExecutor command) {
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