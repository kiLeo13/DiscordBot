package bot.commands;

import bot.util.Channels;
import bot.util.Messages;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.awt.*;
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

    public static void help(Message message) {
        MessageChannelUnion channel = message.getChannel();
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = message.getGuild();
        String roleName = "Unknown";

        rolesExist(guild);
        if (requiredRole != null) roleName = requiredRole.getName();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Registration", guild.getIconUrl())
                .setDescription("Este comando irá guiar e dar exemplos sobre como utilizar o bot para registro.")
                .addField("> 📝 Requisitos", "Para executar este comando, requer `Permission.MESSAGE_ROLES` ou o cargo `" + roleName + "`.", true)
                .addField("> ❓ O que é", "É um comando feito para divulgar a live do Bigo na Twitch *(comando sujeito à remoção em caso de baixa frequência de usos)*.", true)
                .addField("> ❗ Disclaimer", "Este comando NÃO É feito com a intenção de marcar everyone quando utilizado.", true)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public static void run(Message message) {
        Member member = message.getMember();
        User author = message.getAuthor();
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        boolean rolesExist = rolesExist(guild);

        if (author.isBot() || member == null) return;

        if (channel.getIdLong() != Channels.REGISTER_CHANNEL && !member.hasPermission(Permission.ADMINISTRATOR)) return;
        if (!member.getRoles().contains(requiredRole) && !member.hasPermission(Permission.MANAGE_ROLES)) return;

        try { areRolesSetupProperly(message); }
        catch (IllegalArgumentException | HierarchyException e) {
            sendExpireMessage(channel, e.getMessage(), 10000);
            message.delete().queue();
            return;
        }

        if (!isInputValid(message)) {
            sendExpireMessage(channel, "O padrão de registro usado `" + content + "` não é válido.", 5000);
            message.delete().queue();
            return;
        }

        if (rolesExist) {
            if (isInputExact(message)) performExact(message);
            else performDynamic(message);
            return;
        }

        sendExpireMessage(channel, Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.toMessage(), 5000);
        message.delete().queue();
    }

    private static void performExact(Message message) {

        String content = message.getContentRaw();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        Guild guild = message.getGuild();

        // Which roles should we give and take?
        List<Role> toGiveRoles = new ArrayList<>();
        List<Role> toTakeRoles = List.of(verified, notRegistered);

        Member target = guild.retrieveMemberById(args[1].replaceAll("[^0-9]+", "")).complete();

        if (target == null) {
            sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.toMessage(), 5000);
            message.delete().queue();
            return;
        }

        // Why would someone register a bot?
        if (target.getUser().isBot()) {
            message.delete().queue();
            return;
        }

        // Why would someone register themselves?
        if (target.getIdLong() == author.getIdLong()) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> Você não pode registrar você mesmo.", 5000);
            message.delete().queue();
            return;
        }

        // Why would someone register someone that is already registered?
        if (target.getRoles().contains(registered)) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> O membro `" + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + "` já está registrado.", 5000);
            message.delete().queue();
            return;
        }

        // Finnally the register system
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
        guild.modifyMemberRoles(target, toGiveRoles, toTakeRoles).queue();

        logRegister(target, toGiveRoles, toTakeRoles, author);
        logRegister(author, target.getUser(), String.valueOf(genderInput), ageInput, String.valueOf(plataformInput));

        sendExpireMessage(channel,
                "<@" + author.getId() + "> você registrou com sucesso <@" + target.getIdLong() + ">.",
                10000);

        deleteLastMessageByUser(target, channel);
    }

    private static void performDynamic(Message message) {

        String content = message.getContentRaw();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        Guild guild = message.getGuild();

        // Which roles should we give and take?
        List<Role> toGiveRoles = new ArrayList<>();
        List<Role> toTakeRoles = List.of(verified, notRegistered);

        Member target = guild.retrieveMemberById(args[1].replaceAll("[^0-9]+", "")).complete();

        if (target == null) {
            sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.toMessage(), 5000);
            message.delete().queue();
            return;
        }

        // Why would someone register a bot?
        if (target.getUser().isBot()) {
            message.delete().queue();
            return;
        }

        // Why would someone register themselves?
        if (target.getIdLong() == author.getIdLong()) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> Você não pode registrar você mesmo.", 5000);
            message.delete().queue();
            return;
        }

        // Why would someone register someone that is already registered?
        if (target.getRoles().contains(registered)) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> O membro `" + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + "` já está registrado.", 5000);
            message.delete().queue();
            return;
        }

        // Finnally the register system
        char genderInput = registerArgs[0].toLowerCase().charAt(0);
        String ageFormatted = formatAge(registerArgs);
        int ageInput = Integer.parseInt(Arrays.toString(registerArgs).replaceAll("[^\\d+\\-]", ""));
        char plataformInput = registerArgs[registerArgs.length-1].toLowerCase().charAt(0);

        // What? Are you -2 years old?
        if (ageInput < 0) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> você não pode colocar uma idade negativa.", 5000);
            message.delete().queue();
            return;
        }

        // Gender
        switch (genderInput) {
            case 'f' -> toGiveRoles.add(female);
            case 'm' -> toGiveRoles.add(male);
            case 'n' -> toGiveRoles.add(nonBinary);
        }

        // Age
        if (ageInput < 13) {
            toGiveRoles.add(underage);
            toGiveRoles.add(under13);
        }
        if (ageInput >= 13 && ageInput < 18) toGiveRoles.add(underage);
        if (ageInput >= 18) toGiveRoles.add(adult);

        // Plataform
        switch (plataformInput) {
            case 'm' -> toGiveRoles.add(mobile);
            case 'p' -> toGiveRoles.add(pc);
        }

        toGiveRoles.add(registered);
        message.delete().queue();

        // Give and take the provided roles
        guild.modifyMemberRoles(target, toGiveRoles, toTakeRoles).queue();

        logRegister(target, toGiveRoles, toTakeRoles, author);
        logRegister(author, target.getUser(), String.valueOf(genderInput), ageFormatted, String.valueOf(plataformInput));

        sendExpireMessage(channel,
                "<@" + author.getId() + "> você registrou com sucesso <@" + target.getIdLong() + ">.",
                10000);

        deleteLastMessageByUser(target, channel);
    }

    private static boolean isInputExact(Message message) {
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String ageInput = args[0].substring(3, args[0].length()-1);
        List<String> ages = List.of("-13", "-18", "+18");

        return ages.contains(ageInput);
    }

    private static String formatAge(String[] registerArgs) {
        int age = Integer.parseInt(Arrays.toString(registerArgs).replaceAll("[^\\d+\\-]", ""));
        String returned = "Unknown";

        if (age < 13) returned = "-13";
        if (age >= 13 && age < 18) returned = "-18";
        if (age >= 18) returned = "+18";

        return returned;
    }

    private static boolean isInputValid(Message message) {
        String content = message.getContentRaw();
        String[] args = content.split(" ");

        if (args.length < 2) return false;
        if (args[0].substring(2).length() <= 2) return false;

        String[] registrationArgs = args[0].substring(2).split("");
        String ageInput = args[0].substring(3, args[0].length()-1);

        try { Integer.parseInt(ageInput);}
        catch (NumberFormatException e) { return false; }

        List<String> begins = List.of("f", "m", "n");
        List<String> ends = List.of("m", "p");

        return begins.contains(registrationArgs[0]) && ends.contains(registrationArgs[registrationArgs.length - 1]);
    }

    private static boolean rolesExist(Guild guild) {
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
            if (guild.getRoleById(i.get()) == null) return false;

        return true;
    }

    private static void areRolesSetupProperly(Message message) {
        RegistrationRoles[] roles = RegistrationRoles.values();
        Guild guild = message.getGuild();

        for (RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.get());
            Role selfHighest = guild.getSelfMember().getRoles().get(0);

            if (targetRole == null)
                throw new IllegalArgumentException("Role " + r.name() + " cannot be null");

            if (targetRole.getPosition() > selfHighest.getPosition() || !guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES))
                throw new HierarchyException(targetRole.getName() + " has a higher position than my highest role or I don't have Permission.MANAGE_ROLES enabled");
        }
    }

    private static void logRegister(Member target, List<Role> givenRoles, List<Role> removedRoles, User registerMaker) {
        EmbedBuilder builder = new EmbedBuilder();
        String targetName = target.getUser().getName();
        String targetDiscriminator = target.getUser().getDiscriminator();
        String staffName = registerMaker.getName();
        String staffDiscriminator = registerMaker.getDiscriminator();
        Guild guild = target.getGuild();
        TextChannel channel = target.getGuild().getTextChannelById(Channels.REGISTER_LOG_CHANNEL);

        builder
                .setColor(Color.GREEN)
                .setThumbnail(target.getEffectiveAvatarUrl())
                .setTitle("`" + targetName + "#" + targetDiscriminator + "` foi registrado!")
                .setDescription("Registrado por `" + staffName + "#" + staffDiscriminator + "`")
                .addField("> **Cargos Dados**", getFormattedRolesToEmbed(givenRoles), true)
                .addField("> **Cargos Removidos**", getFormattedRolesToEmbed(removedRoles), true)
                .setFooter("Oficina Myuu・ID: " + target.getIdLong(), guild.getIconUrl());

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        else System.out.println("Não foi possível salvar o registro pois nenhum chat foi encontrado.");
    }

    private static void logRegister(User moderator, User target, String gender, String age, String plataform) {
        System.out.println("\n" + moderator.getName() + "#" + moderator.getDiscriminator() +
                " registrou o membro " + target.getName() + "#" + target.getDiscriminator() +
                "\n\nCargos:" +
                "\nGênero: " + getFullGender(gender) +
                "\nIdade: " + getFullAge(age) +
                "\nPlataforma: " + getFullPlataform(plataform));
    }

    private static String getFormattedRolesToEmbed(List<Role> roles) {
        StringBuilder builder = new StringBuilder();

        for (Role r : roles) {
            builder.append("<@&")
                    .append(r.getIdLong())
                    .append(">\n");
        }

        return builder.toString().stripTrailing();
    }

    private static void deleteLastMessageByUser(Member target, MessageChannelUnion channel) {
        List<Message> history = channel.asTextChannel().getHistory().retrievePast(20).complete();

        for (Message m : history) {
            if (m.getAuthor().getIdLong() != target.getUser().getIdLong()) continue;

            m.delete().queue();
            break;
        }
    }

    private static String getFullPlataform(String plataform) {
        String returned = "Unknown";

        switch (plataform) {
            case "m" -> returned = "Mobile";

            case "p" -> returned = "Computador";
        }

         return returned;
    }

    private static String getFullAge(String age) {
        String returned = "Unknown";

        switch (age) {
            case "-13" -> returned = "Menor de idade + (😻)";

            case "-18" -> returned = "Menor de idade";

            case "+18" -> returned = "Maior de idade";
        }

        return returned;
    }

    private static String getFullGender(String gender) {
        String returned = "Unknown";

        switch (gender) {
            case "f" -> returned = "Feminino";

            case "m" -> returned = "Masculino";

            case "n" -> returned = "Não binário";
        }

        return returned;
    }
}