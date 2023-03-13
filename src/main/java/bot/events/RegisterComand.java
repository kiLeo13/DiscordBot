package bot.events;

import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

public class RegisterComand extends ListenerAdapter {
    // Gender
    private Role male;
    private Role female;
    private Role nonBinary;

    // Age
    private Role adult;
    private Role underage;
    private Role under13;

    // Plataform
    private Role pc;
    private Role mobile;

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message content = event.getMessage();
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        boolean isBot = user.isBot();
        MessageChannelUnion channel = event.getChannel();

        if (!isBot) registerAgeFilter(event);

        // Register command
        if (message.toLowerCase(Locale.ROOT).startsWith("r!") && !isBot) {
            if (!rolesExist(event.getGuild())) {
                content.delete().queue();

                channel.sendMessage("One or more register roles were not found, we are sorry about that.")
                        .delay(Duration.ofMillis(5000))
                        .flatMap(Message::delete).queue();

                return;
            }

            registerCommand(event);
        }
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

        User author = e.getAuthor();
        Message content = e.getMessage();
        String message = e.getMessage().getContentRaw();
        String[] args = message.split(" ");
        MessageChannelUnion channel = e.getChannel();
        Guild guild = e.getGuild();
        Member member = e.getMember();

        // Roles
        Role requiredRole = guild.getRoleById(Roles.ROLE_REQUIRED.get());

        if (requiredRole == null) {
            System.out.println(
                    author.getName() + " tried to register someone but the required role was not found."
            );

            content.delete().queue();

            channel.sendMessage("<@" + author.getId() + "> The required role for this action was not found, we are sorry about that.")
                    .delay(Duration.ofMillis(5000))
                    .flatMap(Message::delete).queue();
            return;
        }

        // If author does not have permission, ignore it
        if (member == null) return;

        // Also ignore if member does not the permission at all
        if (member.hasPermission(Permission.MANAGE_ROLES) || !member.getRoles().contains(requiredRole)) return;

        if (args.length < 2) {
            content.delete().queue();
            return;
        }

        String targetRegex = args[1].replaceAll("[^0-9]+", "");
        Member target;

        try {
            target = guild.getMemberById(targetRegex);
        } catch (IllegalArgumentException exception) {
            target = null;
        }

        // If target is not found
        if (target == null) {
            channel.sendMessage("<@" + author.getId() + "> Member `" + args[1] + "` was not found.")
                    .delay(Duration.ofMillis(5000))
                    .flatMap(Message::delete).queue();

            content.delete().queue();

            System.out.println("Moderator " + author.getName() +
                    "#" + author.getDiscriminator() +
                    " tried to register a not found user (" + args[1] + ").");
            return;
        }

        target.getRoles().add(male);








    }

    private boolean rolesExist(Guild guild) {
        try {
            male = guild.getRoleById(Roles.ROLE_MALE.get());
            female = guild.getRoleById(Roles.ROLE_FEMALE.get());
            nonBinary = guild.getRoleById(Roles.ROLE_NON_BINARY.get());

            adult = guild.getRoleById(Roles.ROLE_ADULT.get());
            underage = guild.getRoleById(Roles.ROLE_UNDERAGE.get());
            under13 = guild.getRoleById(Roles.ROLE_UNDER13.get());

            pc = guild.getRoleById(Roles.ROLE_COMPUTER.get());
            mobile = guild.getRoleById(Roles.ROLE_MOBILE.get());
        } catch (IllegalArgumentException | NullPointerException error) {
            return false;
        }

        return true;
    }
}