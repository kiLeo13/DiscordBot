package bot.events;

import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RegisterComand extends ListenerAdapter {

    @SubscribeEvent
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

        Role requiredRole = e.getGuild().getRoleById(Roles.ROLE_REQUIRED.get());

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
        String[] args = message.split(" ");
        MessageChannelUnion channel = e.getChannel();

        if (args.length < 2) {
            channel.sendMessage("Burrão").deadline(System.currentTimeMillis() + 3000).queue();
            return;
        }

        Guild guild = e.getGuild();
        String targetArray = args[1];
        Member target = guild.getMemberById(targetArray.substring(2, targetArray.length()-1));

        if (target == null) {
            channel.sendMessage("Member `" + args[1] + "` not found")
                    .queueAfter(5000, TimeUnit.MILLISECONDS);
            return;
        }

        System.out.println("Member to be registered: " + target.getEffectiveName());










    }
}