package bot.events;

import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class RegisterComand extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        boolean isBot = user.isBot();

        if (!isBot) registerAgeFilter(event);

        // Register command
        if (message.toLowerCase(Locale.ROOT).startsWith("r!") && !isBot)
            registerCommand(event);

    }

    private void registerAgeFilter(MessageReceivedEvent e) {

        Message content = e.getMessage();
        Member member = e.getMember();
        List<String> message = List.of(content.getContentRaw().split(" "));

        if (member == null) return;

        Role requiredRole = member.getGuild().getRoleById(Roles.ROLE_REQUIRED.get());

        if (member.hasPermission(Permission.MANAGE_SERVER) || member.getRoles().contains(requiredRole)) return;

        for (String i : message) {
            try {
                int number = Integer.parseInt(i);

                if (number > 50 || number < 1) content.delete().queue();
            } catch (NumberFormatException ignored) {}
        }
    }

    private void registerCommand(MessageReceivedEvent e) {

        String message = e.getMessage().getContentRaw();
        MessageChannelUnion channel = e.getChannel();
        


    }
}