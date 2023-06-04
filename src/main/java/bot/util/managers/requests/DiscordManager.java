package bot.util.managers.requests;

import bot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.concurrent.Task;

import java.util.Arrays;
import java.util.List;

public class DiscordManager {
    private DiscordManager() {}

    public static DiscordManager NewManager() {
        return new DiscordManager();
    }

    public Task<List<Member>> members(Guild guild, String... ids) {
        try {
            final List<Long> inputs = Arrays.stream(ids)
                    .map(s -> Long.parseLong(s.replaceAll("[^0-9]+", "")))
                    .toList();

            return guild.retrieveMembersByIds(inputs);
        } catch (NumberFormatException e) {
            return guild.retrieveMembersByIds("");
        }
    }

    public CacheRestAction<User> user(String arg) {
        if (arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.isBlank()) return null;

        try {
            return Main.getApi().retrieveUserById(arg);
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public Role roleOf(Guild guild, String arg) {
        if (arg == null) return null;
        arg = arg.replaceAll("[^0-9]+", "");

        if (arg.isBlank()) return null;

        return guild.getRoleById(arg);
    }
}