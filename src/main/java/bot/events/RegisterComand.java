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
    private Role requiredRole;

    // Final Register
    private Role notRegistered;
    private Role registered;
    private Role verified;

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
            if (rolesExist(event.getGuild())) {
                registerCommand(event);
                return;
            }

            content.delete().queue();

            channel.sendMessage("One or more required roles for this action were not found, we are sorry about that.")
                    .delay(Duration.ofMillis(5000))
                    .flatMap(Message::delete).queue();
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

        // If author does not have permission, ignore it
        if (member == null) return;

        // Also ignore if member does not the permission at all
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !member.getRoles().contains(requiredRole)) return;

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

        // Is member already registered?
        if (target.getRoles().contains(registered)) {
            channel.sendMessage("<@" + author.getId() + "> this member is already registered.")
                    .delay(Duration.ofMillis(5000))
                    .flatMap(Message::delete)
                    .queue();

            content.delete().queue();

            System.out.println("Moderator " + author.getName() + "#" + author.getDiscriminator() + " tentou registrar " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + " mas ele já estava registrado.");
            return;
        }

        String[] registerArgs = args[0].substring(2).split("");

        try {
            checkRegisterInput(registerArgs);
        } catch (IllegalArgumentException exception) {
            content.delete().queue();

            channel.sendMessage("<@" + author.getId() + "> invalid register format.\nSee: `" + args[0] + "`.")
                    .delay(Duration.ofMillis(5000))
                    .flatMap(Message::delete)
                    .queue();

            System.out.println("Moderator " + author.getName() + "#" + author.getDiscriminator() + " used an invalid register format.\nSee: " + args[0]);
            return;
        }

        char genderInput = registerArgs[0].charAt(0);
        String ageInput = registerArgs[1] + registerArgs[2] + registerArgs[3];
        char plataformInput = registerArgs[4].charAt(0);

        // Gender
        switch (genderInput) {
            case 'f' -> guild.addRoleToMember(target, female).queue();
            case 'm' -> guild.addRoleToMember(target, male).queue();
            case 'n' -> guild.addRoleToMember(target, nonBinary).queue();
        }

        // Age
        switch (ageInput) {
            case "-13" -> guild.addRoleToMember(target, under13).queue();
            case "-18" -> guild.addRoleToMember(target, underage).queue();
            case "+18" -> guild.addRoleToMember(target, adult).queue();
        }

        // Plataform
        switch (plataformInput) {
            case 'b' -> {
                guild.addRoleToMember(target, mobile).queue();
                guild.addRoleToMember(target, pc).queue();
            }
            case 'm' -> guild.addRoleToMember(target, mobile).queue();
            case 'p' -> guild.addRoleToMember(target, pc).queue();
        }

        guild.addRoleToMember(target, registered).queue();
        guild.removeRoleFromMember(target, notRegistered).queue();
        guild.removeRoleFromMember(target, verified).queue();

        System.out.println(author.getName() +
                "#" + author.getDiscriminator() +
                " registrou o membro " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + "!\nId do registrado: " + target.getId() + "\n\nCargos:" +
                "\nGênero: " + getFullGender(genderInput) +
                "\nIdade: " + getFullAge(ageInput) +
                "\nPlataforma: " + getFullPlataform(plataformInput));
    }

    private boolean rolesExist(Guild guild) {
        try {
            requiredRole = guild.getRoleById(Roles.ROLE_REQUIRED.get());

            notRegistered = guild.getRoleById(Roles.ROLE_NOT_REGISTERED.get());
            registered = guild.getRoleById(Roles.ROLE_REGISTERED.get());
            verified = guild.getRoleById(Roles.ROLE_VERIFIED.get());

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

    private void checkRegisterInput(String[] input) throws IllegalArgumentException {
        List<String> gender = List.of("f", "m", "n");
        List<String> age = List.of("-13", "-18", "+18");
        List<String> plataform = List.of("b", "m", "p");

        if (input.length != 5) throw new IllegalArgumentException("Too few arguments");

        if (!gender.contains(input[0])) throw new IllegalArgumentException("Could not find gender '" + input[0] + "'");
        if (!age.contains(input[1] + input[2] + input[3])) throw new IllegalArgumentException("Could not find age '" + input[0] + input[1] + input[2] + "'");
        if (!plataform.contains(input[4])) throw new IllegalArgumentException("could not find plataform '" + input[4] + "'");
    }

    private String getFullGender(char gender) {
        switch (gender) {
            case 'f' -> {
                return "Feminino";

            }

            case 'm' -> {
                return "Masculino";
            }

            case 'n' -> {
                return "Não Binário";
            }

            default -> {
                return "Unknown";
            }
        }
    }

    private String getFullAge(String age) {
        switch (age) {
            case "-13" -> {
                return "Menor de idade + (😻)";
            }

            case "-18" -> {
                return "Menor de Idade";
            }

            case "+19" -> {
                return "Maior de Idade";
            }

            default -> {
                return "Unknown";
            }
        }
    }

    private String getFullPlataform(char plataform) {
        switch (plataform) {
            case 'b' -> {
                return "Ambos (PC e Mobile)";
            }

            case 'm' -> {
                return "Mobile";
            }

            case 'p' -> {
                return "Computador";
            }

            default -> {
                return "Unknown";
            }
        }
    }
}