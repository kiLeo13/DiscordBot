package bot.events.handlers;

import bot.util.Requirements;
import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class AgeFilter {
    private AgeFilter() {}

    public static void perform(Message message) {

        List<Long> filterChannels = Requirements.REGISTER_FILTER_CHANNELS.get();
        if (filterChannels.isEmpty()) return;

        Member member = message.getMember();
        List<String> args = List.of(message.getContentRaw().split(" "));
        MessageChannelUnion channel = message.getChannel();

        if (!filterChannels.contains(channel.getIdLong())) return;
        if (member == null) return;

        Role requiredRole = message.getGuild().getRoleById(Roles.ROLE_REQUIRED.get());

        if (member.hasPermission(Permission.MANAGE_SERVER) || member.getRoles().contains(requiredRole)) return;

        for (String i : args) {
            try {
                int number = Integer.parseInt(i);

                if (number > 50 || number < 1) message.delete().queue();
            } catch (NumberFormatException ignore) {}
        }
    }
}