package bot.commands.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class RoleAmongUs {
    private RoleAmongUs() {}

    protected static final MessageEmbed HELP = help();

    private static MessageEmbed help() {
        EmbedBuilder builder = new EmbedBuilder();

        return builder.build();
    }
}