package bot.commands;

import bot.util.Requirements;
import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static bot.data.BotConfig.isRegisterEnabled;
import static bot.util.Extra.sendExpireMessage;

public class Registration {
    private static Role requiredRole;

    // Final Register
    private static Role notRegistered;
    private static Role registered;
    private static Role verified;

    // Gender
    private static Role male;
    private static Role female;
    private static Role nonBinary;

    // Age
    private static Role adult;
    private static Role underage;
    private static Role under13;

    // Plataform
    private static Role pc;
    private static Role mobile;
    private Registration() {}

    public static void perform(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        boolean isBot = author.isBot();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        if (isBot) return;
        if (!isRegisterEnabled(guild)) return;

        // Special
        String messageLink = "https://discord.com/channels/" + guild.getId() + "/" + channel.getId() + "/" + message.getId();
        String data = getFormattedData();

        if (member == null) return;

        // Ignore if member does not the permission
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !member.getRoles().contains(requiredRole)) {
            System.out.println("Um membro sem permissão tentou usar o registro.\n" +
                    "\nMembro: @" + author.getName() + "#" + author.getDiscriminator() +
                    "\nID: " + author.getId() +
                    "\nChat: #" + channel.getName() +
                    "\nComando: " + message +
                    "\nLink da Mensagem: " + messageLink +
                    "\nData: " + data);
            return;
        }

        if (!Requirements.REGISTER_CHANNELS.get().contains(channel.getIdLong())) return;

        // Start register process if everything is fine
        if (rolesExist(guild)) {
            run(message);
            return;
        }

        message.delete().queue();

        sendExpireMessage(channel,
                "One or more required roles for this action were not found, we are sorry about that.",
                5000);
    }

    private static void run(Message message) {

        List<Long> allowedRegisterChannels = Requirements.REGISTER_CHANNELS.get();
        if (allowedRegisterChannels.isEmpty()) return;

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        MessageChannelUnion channel = message.getChannel();

        User author = message.getAuthor();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        // If member is not find, ignore it
        if (member == null) return;

        if (args.length < 2) {
            message.delete().queue();
            return;
        }

        String targetRegex = args[1].replaceAll("[^0-9]+", "");
        Member target;

        // If target is not found or something very weird happens
        try {
            target = guild.retrieveMemberById(targetRegex).complete();

            if (target == null) throw new IllegalArgumentException("Target cannot be null");
        } catch (ErrorResponseException | IllegalArgumentException exception) {
            sendExpireMessage(channel, "<@" + author.getId() + "> Member `" + args[1] + "` was not found.", 5000);

            message.delete().queue();

            System.out.println("Staff " + author.getName() +
                    "#" + author.getDiscriminator() +
                    " tentou registrar um membro não encontrado (" + args[1] + ").");
            return;
        }

        // Are they trying to register themselves?
        if (target.getIdLong() == member.getIdLong()) {
            message.delete().queue();
            sendExpireMessage(channel, "<@" + author.getId() + "> you cannot register yourself.", 5000);
            System.out.println("Staff " + author.getName() + "#" + author.getDiscriminator() + " tentou se auto registrar.");
            return;
        }

        // Is member already registered?
        if (target.getRoles().contains(registered)) {
            sendExpireMessage(channel, "<@" + author.getId() + "> this member is already registered.", 5000);

            message.delete().queue();

            System.out.println("Staff " + author.getName() +
                    "#" + author.getDiscriminator() +
                    " tentou registrar " + target.getUser().getName() +
                    "#" + target.getUser().getDiscriminator() +
                    " mas ele já estava registrado. Ignorando...");
            return;
        }

        if (target.getUser().isBot()) {
            message.delete().queue();
            System.out.println("Staff " + author.getName() +
                    "#" + author.getDiscriminator() +
                    " tentou registrar um bot: " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator());
            return;
        }

        try {
            checkRegisterInput(registerArgs);
        } catch (IllegalArgumentException exception) {
            message.delete().queue();

            sendExpireMessage(channel, "<@" + author.getId() + "> invalid register format.\nSee: `" + args[0] + "`.", 5000);

            System.out.println("Staff " + author.getName() + "#" + author.getDiscriminator() + " utilizou um formato de registro inválido.\nVeja: " + args[0]);
            return;
        }

        char genderInput = registerArgs[0].toLowerCase().charAt(0);
        String ageInput = Arrays.toString(registerArgs).replaceAll("[^\\d+\\-]", "");
        char plataformInput = registerArgs[4].toLowerCase().charAt(0);

        // Gender
        switch (genderInput) {
            case 'f' -> guild.addRoleToMember(target, female).queue();
            case 'm' -> guild.addRoleToMember(target, male).queue();
            case 'n' -> guild.addRoleToMember(target, nonBinary).queue();
        }

        // Age
        switch (ageInput) {
            case "-13" -> {
                guild.addRoleToMember(target, under13).queue();
                guild.addRoleToMember(target, underage).queue();
            }
            case "-18" -> guild.addRoleToMember(target, underage).queue();
            case "+18" -> guild.addRoleToMember(target, adult).queue();
        }

        // Plataform
        switch (plataformInput) {
            case 'm' -> guild.addRoleToMember(target, mobile).queue();
            case 'p' -> guild.addRoleToMember(target, pc).queue();
        }

        message.delete().queue();

        // Give registered role
        guild.addRoleToMember(target, registered).queue();

        // Take not registered role
        guild.removeRoleFromMember(target, notRegistered).queue();

        // Take verified role
        guild.removeRoleFromMember(target, verified).queue();

        sendExpireMessage(channel,
                "<@" + author.getId() + "> member `" + target.getEffectiveName() +
                        "#" + target.getUser().getDiscriminator() +
                        "` has been sucessfully registered.",
                10000);

        System.out.println(author.getName() +
                "#" + author.getDiscriminator() +
                " registrou o membro " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + "!\nId do registrado: " + target.getId() + "\n\nCargos:" +
                "\nGênero: " + getFullGender(genderInput) +
                "\nIdade: " + getFullAge(ageInput) +
                "\nPlataforma: " + getFullPlataform(plataformInput));
    }

    private static boolean rolesExist(Guild guild) {
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

            // Were all roles found?
            Roles[] roles = Roles.values();

            for (Roles i : roles)
                if (guild.getRoleById(i.get()) == null) throw new IllegalArgumentException("Role '" + i + "' cannot be null");

        } catch (IllegalArgumentException | NullPointerException ignore) { return false; }

        return true;
    }

    private static void checkRegisterInput(String[] input) throws IllegalArgumentException {
        List<String> gender = List.of("f", "m", "n");
        List<String> age = List.of("-13", "-18", "+18");
        List<String> plataform = List.of("m", "p");

        if (input.length != 5) throw new IllegalArgumentException("Too few arguments");

        if (!gender.contains(input[0])) throw new IllegalArgumentException("Could not find gender '" + input[0] + "'");
        if (!age.contains(input[1] + input[2] + input[3])) throw new IllegalArgumentException("Could not find age '" + input[0] + input[1] + input[2] + "'");
        if (!plataform.contains(input[4])) throw new IllegalArgumentException("could not find plataform '" + input[4] + "'");
    }

    private static String getFullGender(char gender) {
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

    private static String getFullAge(String age) {
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

    private static String getFullPlataform(char plataform) {
        switch (plataform) {
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

    private static String getFormattedData() {

        String year = formatNumber(LocalDateTime.now().getYear());
        String month = formatNumber(LocalDateTime.now().getMonth().getValue());
        String day = formatNumber(LocalDateTime.now().getDayOfMonth());
        String hour = formatNumber(LocalDateTime.now().getHour());
        String minute = formatNumber(LocalDateTime.now().getMinute());
        String second = formatNumber(LocalDateTime.now().getSecond());

        /*
         * Example: January 04th, 2023 at 5:34:21 PM
         * 01/04/2023 às 17h 34m 21s
         */
        return day + "/" + month + "/" + year + " às " + hour + "h " + minute + "m " + second + "s";
    }

    private static String formatNumber(int num) {
        return num < 10
                ? "0" + num
                : String.valueOf(num);
    }
}