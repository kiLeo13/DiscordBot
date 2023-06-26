package bot.internal.abstractions;

import bot.internal.data.BotData;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.stream.Stream;

public abstract class BotCommand implements CommandExecution {
    private final boolean findable;
    private final int minLength;
    private final String usage;
    private final List<String> names;
    private final Permission permission;

    public BotCommand(boolean findable, int length, Permission permission, String usage, String... calls) {

        if (calls == null || calls.length < 1)
            throw new IllegalArgumentException("Commands must have at least 1 name");

        this.permission = permission;
        this.minLength = length;
        this.findable = findable;
        this.names = Stream.of(calls)
                .map(e -> e.replace("{pf}", BotData.PREFIX))
                .toList();
        this.usage = usage == null
                ? this.names.get(0)
                : usage;
    }

    public BotCommand(boolean findable, String usage, String... calls) {
        this(findable, 0, null, usage, calls);
    }

    public BotCommand(String usage, String... calls) {
        this(true, usage, calls);
    }

    public final List<String> getNames() {
        return this.names;
    }

    /**
     * @return Whether the command has public access or is possible to be found on {@code /help} commands.
     */
    public final boolean isPublic() {
        return this.findable;
    }

    public final Permission getRequiredPermission() {
        return this.permission;
    }

    public final String getUsage() {
        return this.usage;
    }

    /**
     * @return The minimum amount of arguments a command is supposed to have when called by a member.
     */
    public final int getMinLength() {
        return this.minLength;
    }
}