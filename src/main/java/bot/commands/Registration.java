package bot.commands;

import bot.util.Channels;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void run(Message message) {

        String content = message.getContentRaw();
        User author = message.getAuthor();
        Member member = message.getMember();
        boolean isBot = author.isBot();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        boolean rolesExist = rolesExist(guild);

        if (isBot) return;

        // Special
        String messageLink = "https://discord.com/channels/" + guild.getId() + "/" + channel.getId() + "/" + message.getId();
        String data = getFormattedData();

        if (member == null) return;
        if (!Channels.REGISTER_CHANNELS.contains(channel.getIdLong())) return;

        // Ignore if member does not the permission
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !member.getRoles().contains(requiredRole)) {
            System.out.println("Um membro sem permissão tentou usar o registro.\n" +
                    "\nMembro: " + author.getName() + "#" + author.getDiscriminator() +
                    "\nID: " + author.getId() +
                    "\nChat: #" + channel.getName() +
                    "\nComando: " + content +
                    "\nLink da Mensagem: " + messageLink +
                    "\nData: " + data);
            return;
        }

        if (!isRegistrationAvailable(message)) return;

        // Start register process if everything is fine
        if (rolesExist) {
            perform(message);
            return;
        }

        message.delete().queue();

        sendExpireMessage(channel,
                "One or more required roles for this action were not found, we are sorry about that.",
                5000);
    }

    private static void perform(Message message) {

        List<Long> allowedRegisterChannels = Channels.REGISTER_CHANNELS;
        if (allowedRegisterChannels.isEmpty()) return;

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        MessageChannelUnion channel = message.getChannel();
        ArrayList<Role> toGiveRoles = new ArrayList<>();
        List<Role> toRemoveRoles = List.of(verified, notRegistered);

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
            return;
        }

        if (target.getUser().isBot()) {
            message.delete().queue();
            return;
        }

        try {
            checkRegisterInput(registerArgs);
        } catch (IllegalArgumentException exception) {
            message.delete().queue();

            sendExpireMessage(channel, "<@" + author.getId() + "> invalid register format.\nSee: `" + args[0] + "`.", 5000);
            return;
        }

        char genderInput = registerArgs[0].toLowerCase().charAt(0);
        String ageInput = Arrays.toString(registerArgs).replaceAll("[^\\d+\\-]", "");
        char plataformInput = registerArgs[4].toLowerCase().charAt(0);

        // Gender
        switch (genderInput) {
            case 'f' -> toGiveRoles.add(female);
            case 'm' -> toGiveRoles.add(male);
            case 'n' -> toGiveRoles.add(nonBinary);
        }

        // Age
        switch (ageInput) {
            case "-13" -> {
                toGiveRoles.add(under13);
                toGiveRoles.add(underage);
            }
            case "-18" -> toGiveRoles.add(underage);
            case "+18" -> toGiveRoles.add(adult);
        }

        // Plataform
        switch (plataformInput) {
            case 'm' -> toGiveRoles.add(mobile);
            case 'p' -> toGiveRoles.add(pc);
        }

        toGiveRoles.add(registered);
        message.delete().queue();

        // Give and take the provided roles
        guild.modifyMemberRoles(target, toGiveRoles, toRemoveRoles).queue();

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
            requiredRole = guild.getRoleById(RegistrationRoles.ROLE_REQUIRED.get());

            notRegistered = guild.getRoleById(RegistrationRoles.ROLE_NOT_REGISTERED.get());
            registered = guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.get());
            verified = guild.getRoleById(RegistrationRoles.ROLE_VERIFIED.get());

            male = guild.getRoleById(RegistrationRoles.ROLE_MALE.get());
            female = guild.getRoleById(RegistrationRoles.ROLE_FEMALE.get());
            nonBinary = guild.getRoleById(RegistrationRoles.ROLE_NON_BINARY.get());

            adult = guild.getRoleById(RegistrationRoles.ROLE_ADULT.get());
            underage = guild.getRoleById(RegistrationRoles.ROLE_UNDERAGE.get());
            under13 = guild.getRoleById(RegistrationRoles.ROLE_UNDER13.get());

            pc = guild.getRoleById(RegistrationRoles.ROLE_COMPUTER.get());
            mobile = guild.getRoleById(RegistrationRoles.ROLE_MOBILE.get());

            // Were all roles found?
            RegistrationRoles[] roles = RegistrationRoles.values();

            for (RegistrationRoles i : roles)
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

        LocalDateTime time = LocalDateTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return time.format(dateFormatter) + " às " + time.format(timeFormatter);
    }

    private static boolean isRegistrationAvailable(Message message) {
        RegistrationRoles[] roles = RegistrationRoles.values();
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();

        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) return false;

        for (RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.get());
            Role selfHighest = guild.getSelfMember().getRoles().get(0);

            if (targetRole == null) {
                System.out.println("Não foi possível completar registro, cargo " + r.name() + " não foi encontrado.");
                sendExpireMessage(channel, "Could not find role `" + r.name() + "`, we're sorry about that.", 10000);
                return false;
            }

            if (targetRole.getPosition() > selfHighest.getPosition()) {
                System.out.println("Um ou mais cargos de registro estão acima do meu, por favor, reajuste a hierarquia do servidor.");
                sendExpireMessage(channel, "One or more roles have a higher position than my highest role. Please adjust the server hierarchy accordingly.", 10000);
                return false;
            }
        }
        return true;
    }
}