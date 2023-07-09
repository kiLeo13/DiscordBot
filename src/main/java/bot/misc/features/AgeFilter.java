package bot.misc.features;

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

public class AgeFilter extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        
        Message message = event.getMessage();
        Member member = message.getMember();
        String contentNumbers = message.getContentRaw().replaceAll("[^0-9 ]+", "");
        MessageChannelUnion channel = message.getChannel();
        User author = message.getAuthor();

        if (!Channels.AGE_FILTER_CHANNELS.ids().contains(channel.getId())) return;
        if (member == null || author.isBot()) return;

        Role requiredRole = message.getGuild().getRoleById(RegistrationRoles.REQUIRED.id());

        if (member.hasPermission(Permission.MANAGE_SERVER) || (requiredRole != null && member.getRoles().contains(requiredRole))) return;

        if (contentNumbers.isBlank())
            Bot.delete(message);
    }
}