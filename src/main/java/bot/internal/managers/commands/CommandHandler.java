package bot.internal.managers.commands;

import bot.commands.Register;
import bot.internal.data.BotData;
import bot.util.Bot;
import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.internal.abstractions.annotations.MessageDeletion;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler extends ListenerAdapter {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(5);
    private static final Map<List<String>, BotCommand> commands = new HashMap<>();
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

        if (!content.startsWith(BotData.PREFIX) && !content.startsWith("r!")) return;

        // Run command (on another thread)
        EXECUTOR.execute(() -> runCommand(message));
    }

    private void runCommand(Message message) {

        Member member = message.getMember();
        String input = message.getContentRaw().toLowerCase();
        String cmd = input.split(" ")[0];
        String[] args = input.split(" ");
        String[] cmdArgs = args.length < 2
                ? new String[]{}
                : input.substring(cmd.length() + 1).split(" ");

        // Registration command is a specific case
        if (cmd.startsWith("r!")) {
            Register.getInstance().run(message, cmdArgs);
            Bot.delete(message);
            return;
        }

        BotCommand command = findCommand(cmd);
        if (command == null) return;

        if (hasPermission(member, command)) {
            MessageDeletion annotation = command.getClass().getAnnotation(MessageDeletion.class);

            if (annotation == null || annotation.value())
                Bot.delete(message);

            command.run(message, cmdArgs);
        }
    }

    private boolean hasPermission(Member member, BotCommand command) {
        final List<Permission> permissions = List.of(command.getClass().getAnnotation(CommandPermission.class).permissions());

        if (permissions.isEmpty()) return true;

        for (Permission p : permissions)
            if (member.hasPermission(p))
                return true;

        return false;
    }

    private BotCommand findCommand(String cmd) {
        for (List<String> cmds : commands.keySet()) {
            BotCommand command = commands.get(cmds);

            if (cmds.contains(cmd))
                return command;
        }

        return null;
    }

    /**
     * Registers the commands the bot will listen to.
     *
     * @param input The commands to be registered.
     */
    public void registerCommands(BotCommand... input) {
        for (BotCommand command : input) {
            List<String> names = command.getNames()
                    .stream()
                    .map(s -> s
                            .replace("<pf>", BotData.PREFIX)
                            .toLowerCase()
                    ).toList();

            for (String n : names)
                if (n == null || n.toLowerCase().split(" ").length != 1)
                    throw new IllegalArgumentException("Command names cannot contain spaces");

            commands.put(names, command);
        }
    }

    public static Map<List<String>, BotCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }
}