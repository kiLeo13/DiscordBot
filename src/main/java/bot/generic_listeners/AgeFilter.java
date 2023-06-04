package bot.generic_listeners;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.RegistrationRoles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.List;

public class AgeFilter extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        
        List<String> filterChannels = Channels.REGISTER_AGE_FILTER_CHANNELS.ids();

        Message message = event.getMessage();
        Member member = message.getMember();
        String[] args = message.getContentRaw().replaceAll("[^0-9 ]+", "").split(" ");
        MessageChannelUnion channel = message.getChannel();
        boolean hasNumber = false;
        User author = message.getAuthor();

        if (!filterChannels.contains(channel.getId())) return;
        if (member == null || author.isBot()) return;

        Role requiredRole = message.getGuild().getRoleById(RegistrationRoles.ROLE_REQUIRED.id());

        if (member.hasPermission(Permission.MANAGE_SERVER) || member.getRoles().contains(requiredRole)) return;

        for (String i : args) {
            try {
                int number = Integer.parseInt(i);

                // Are you really 30 years old OR EVEN -1 YEAR OLD? :oooo
                if (number > 30 || number < 0) {
                    Bot.delete(message);
                    return;
                }

                hasNumber = true;
            } catch (NumberFormatException ignore) {}
        }

        // Hmmm you're one of those people who never age, right?
        if (!hasNumber) Bot.delete(message);
    }
}