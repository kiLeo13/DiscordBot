package bot.internal.managers.commands;

import bot.commands.Register;
import bot.internal.data.BotData;
import bot.util.Bot;
import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.MessageDeletion;
import bot.util.content.Responses;
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
    private static final List<BotCommand> commands = new ArrayList<>();
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
        String[] rawArgs = input.split(" ");
        String cmd = rawArgs[0];
        String[] cmdArgs = rawArgs.length < 2
                ? new String[]{}
                : input.substring(cmd.length() + 1).split(" ");

        // Registration command is a specific case
        if (cmd.startsWith("r!") || !cmd.startsWith("r!take")) {
            Register.getInstance().run(message, input.substring(2).split(" "));
            Bot.delete(message);
            return;
        }

        BotCommand command = findCommand(cmd);
        if (command == null) return;

        if (hasPermission(member, command)) {
            MessageDeletion annotation = command.getClass().getAnnotation(MessageDeletion.class);

            // Block it if the executed command has fewer arguments provided than the required amount
            if (cmdArgs.length < command.getMinLength()) {
                Bot.tempEmbed(message.getChannel(), Responses.ERROR_TOO_FEW_ARGUMENTS, 10000);
                Bot.delete(message);
                return;
            }

            if (annotation == null || annotation.value())
                Bot.delete(message);

            command.run(message, cmdArgs);
        }
    }

    private boolean hasPermission(Member member, BotCommand command) {
        final Permission permission = command.getRequiredPermission();

        return permission == null || member.hasPermission(permission);
    }

    private BotCommand findCommand(String cmd) {
        for (BotCommand command : commands) {
            List<String> names = command.getNames();

            if (names.contains(cmd))
                return command;
        }

        return null;
    }

    public void registerCommands(BotCommand... input) {
        for (BotCommand command : input) {
            final List<String> names = command.getNames().stream().map(String::toLowerCase).toList();

            for (String n : names) {

                if (n == null || n.split(" ").length != 1)
                    throw new IllegalArgumentException("Command names cannot contain spaces");
            }

            commands.add(command);
        }
    }

    /**
     * Returns the command with the provided name.
     *
     * @param name The name of the command. <b>You MUST provide the prefix</b>.
     * @return A possible-null {@link BotCommand} instance with the command.
     */
    public static BotCommand getCommand(String name) {

        for (BotCommand cmd : commands) {
            List<String> cmdName = cmd.getNames();

            if (cmdName.contains(name))
                return cmd;
        }

        return null;
    }

    public static List<BotCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}