package bot.internal.abstractions;

import bot.internal.abstractions.annotations.CommandPermission;
import bot.internal.data.BotData;

import java.util.List;
import java.util.stream.Stream;

public abstract class BotCommand implements CommandExecution {
    private final boolean findable;
    private final String[] names;

    public BotCommand(boolean findable, String... names) {
        if (!this.getClass().isAnnotationPresent(CommandPermission.class))
            throw new IllegalArgumentException("Annotation " + CommandPermission.class.getName() + " is not present in class " + this.getClass().getName());

        if (names == null || names.length < 1)
            throw new IllegalArgumentException("Commands must have at least 1 name");

        this.names = names;
        this.findable = findable;
    }

    public final List<String> getNames() {
        return Stream.of(this.names)
                .map(e -> e.replace("<pf>", BotData.PREFIX))
                .toList();
    }

    public final boolean isFindable() {
        return this.findable;
    }
}