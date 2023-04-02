package bot.events.handlers;

import bot.util.Channels;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class AgeFilter {
    private AgeFilter() {}

    public static void perform(Message message) {

        List<Long> filterChannels = Channels.REGISTER_AGE_FILTER_CHANNELS.toIds();

        Member member = message.getMember();
        String[] args = message.getContentRaw().replaceAll("[^0-9 ]+", "").split(" ");
        MessageChannelUnion channel = message.getChannel();
        boolean hasNumber = false;

        if (!filterChannels.contains(channel.getIdLong())) return;
        if (member == null) return;

        Role requiredRole = message.getGuild().getRoleById(RegistrationRoles.ROLE_REQUIRED.toId());

        if (member.hasPermission(Permission.MANAGE_SERVER) || member.getRoles().contains(requiredRole)) return;

        for (String i : args) {
            try {
                int number = Integer.parseInt(i);

                // Are you really 30 years old OR EVEN -1 YEAR OLD? :oooo
                if (number > 30 || number < 0) message.delete().queue();
                hasNumber = true;
            } catch (NumberFormatException ignore) {}
        }

        // Hmmm you're one of those people who never age, right?
        if (!hasNumber) message.delete().queue();
    }
}