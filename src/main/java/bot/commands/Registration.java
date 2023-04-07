package bot.commands;

import bot.util.*;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static bot.util.Bot.sendExpireMessage;

public class Registration implements CommandExecutor, SlashExecutor {
    private static Registration INSTANCE;

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

    private Registration() {}

    public static Registration getInstance() {
        if (INSTANCE == null) INSTANCE = new Registration();
        return INSTANCE;
    }

    @Override
    public void run(Message message) {
        Member member = message.getMember();
        User author = message.getAuthor();
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        boolean rolesExist = rolesExist(guild);

        if (author.isBot() || member == null) return;

        // Is the channel correct?
        if (channel.getIdLong() != Channels.REGISTER_CHANNEL.toId()) return;

        if (!member.getRoles().contains(requiredRole) && !member.hasPermission(Permission.MANAGE_ROLES)) return;

        try { areRolesSetupProperly(guild); }
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

        sendExpireMessage(channel, Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message(), 5000);
        message.delete().queue();
    }

    @Override
    public void help(Message message) {
        MessageChannelUnion channel = message.getChannel();
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = message.getGuild();
        String roleName = "[???]";

        rolesExist(guild);
        if (requiredRole != null) roleName = requiredRole.getName();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Registration")
                .setThumbnail(guild.getIconUrl())
                .setDescription("Este comando irá guiar e dar exemplos sobre como utilizar o bot para registro.")
                .addField("> 📝 Requisitos", "Requer `MANAGE_ROLES` ou o cargo `" + roleName + "`.", true)
                .addField("> ❓ O que é", "Desenvolvido para facilitar o sistema de registro da Oficina.", true)
                .addField("> ⚙ Syntax: `r!<parameters> <@user>`", """
                        Ex:
                        `r!m-18p` | `r!m14p` - *Masculino, 14 anos, pc*
                        `r!f+18m` | `r!f23m` - *Feminino, 23 anos, mobile*
                        `r!n-13p` | `r!n9p`  - *Não binário, 9 anos, pc*
                        
                        Final: "r!m16p <@596939790532739075>"
                        """, false)
                .addField("> 📋 Parameters", """
                        > **Gênero:**
                        ```
                        f -> Feminino
                        m -> Masculino
                        n -> Não binário
                        ```
                        > **Idade:**
                        ```
                        -13 -> Menor de idade + (😻)
                        -18 -> Menor de idade
                        +18 -> Maior de idade
                        ```
                        > **Plataforma:**
                        ```
                        m -> Mobile
                        p -> Computador/PC
                        ```
                        """, false)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    private void performExact(Message message) {

        String content = message.getContentRaw();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        Guild guild = message.getGuild();

        // Which roles should we give and take?
        List<Role> toGiveRoles = new ArrayList<>();
        List<Role> toTakeRoles = new ArrayList<>(2);

        Member target;

        try {
            target = guild.retrieveMemberById(args[1].replaceAll("[^0-9]+", "")).complete();
        } catch (ErrorResponseException e) {
            sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
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

        // Adding roles to be takien
        if (target.getRoles().contains(notRegistered)) toTakeRoles.add(notRegistered);
        if (target.getRoles().contains(verified)) toTakeRoles.add(verified);

        toGiveRoles.add(registered);
        message.delete().queue();

        // Give and take the provided roles
        guild.modifyMemberRoles(target, toGiveRoles, toTakeRoles).queue();

        logRegister(target, toGiveRoles, toTakeRoles, author);

        sendExpireMessage(channel,
                "<@" + author.getId() + "> você registrou com sucesso <@" + target.getIdLong() + ">.",
                10000);

        deleteLastMessageByUser(target, channel);
    }

    private void performDynamic(Message message) {

        String content = message.getContentRaw();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        String[] registerArgs = args[0].substring(2).split("");
        Guild guild = message.getGuild();

        // Which roles should we give and take?
        List<Role> toGiveRoles = new ArrayList<>();
        List<Role> toTakeRoles = new ArrayList<>(2);

        Member target;

        try {
            target = guild.retrieveMemberById(args[1].replaceAll("[^0-9]+", "")).complete();
        } catch (ErrorResponseException e) {
            sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
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
        int ageInput = Integer.parseInt(Arrays.toString(registerArgs).replaceAll("[^\\d+\\-]", ""));
        char plataformInput = registerArgs[registerArgs.length-1].toLowerCase().charAt(0);

        // What? Are you -2 years old?
        if (ageInput < 0) {
            sendExpireMessage(channel, "<@" + author.getIdLong() + "> você não pode inserir uma idade negativa.", 5000);
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
        if (ageInput >= 18) toGiveRoles.add(adult);
        if (ageInput < 18) toGiveRoles.add(underage);
        if (ageInput < 13) toGiveRoles.add(under13);

        // Plataform
        switch (plataformInput) {
            case 'm' -> toGiveRoles.add(mobile);
            case 'p' -> toGiveRoles.add(pc);
        }

        // Adding roles to be takien
        if (target.getRoles().contains(notRegistered)) toTakeRoles.add(notRegistered);
        if (target.getRoles().contains(verified)) toTakeRoles.add(verified);

        toGiveRoles.add(registered);
        message.delete().queue();

        // Give and take the provided roles
        guild.modifyMemberRoles(target, toGiveRoles, toTakeRoles).queue();

        logRegister(target, toGiveRoles, toTakeRoles, author);

        sendExpireMessage(channel,
                "<@" + author.getId() + "> você registrou com sucesso <@" + target.getIdLong() + ">.",
                10000);

        deleteLastMessageByUser(target, channel);
    }

    // This is the slash command version
    @Override
    public void runSlash(SlashCommandInteractionEvent event) {
        // We can ignore all these warnings since these options are set as required
        User author = event.getUser();
        String genderInput = event.getOption("gender").getAsString();
        int ageInput = event.getOption("age").getAsInt();
        String plataformInput = event.getOption("plataform").getAsString();
        Member target = event.getOption("target").getAsMember();
        Guild guild = event.getGuild();
        boolean rolesExist = rolesExist(guild);

        if (!rolesExist) {
            event.reply(Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        try { areRolesSetupProperly(guild); }
        catch (IllegalArgumentException | HierarchyException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        }

        // Which roles should we give and take?
        List<Role> toGiveRoles = new ArrayList<>();
        List<Role> toTakeRoles = new ArrayList<>(2);

        if (author.isBot() || target == null) return;

        if (target.getIdLong() == author.getIdLong()) {
            event.reply("Você não pode registrar você mesmo.").setEphemeral(true).queue();
            return;
        }

        if (ageInput < 0) {
            event.reply("Você não pode inserir uma idade negativa.").setEphemeral(true).queue();
            return;
        }

        if (target.getRoles().contains(registered)) {
            event.reply("O membro `" + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + "` já está registrado.").setEphemeral(true).queue();
            return;
        }

        // Gender
        switch (genderInput) {
            case "female" -> toGiveRoles.add(female);
            case "male" -> toGiveRoles.add(male);
            case "nonBinary" -> toGiveRoles.add(nonBinary);
        }

        // Age
        if (ageInput >= 18) toGiveRoles.add(adult);
        if (ageInput < 18) toGiveRoles.add(underage);
        if (ageInput < 13) toGiveRoles.add(under13);

        // Plataform
        switch (plataformInput) {
            case "mobile" -> toGiveRoles.add(mobile);
            case "pc" -> toGiveRoles.add(pc);
        }

        // Adding roles to be takien
        if (target.getRoles().contains(notRegistered)) toTakeRoles.add(notRegistered);
        if (target.getRoles().contains(verified)) toTakeRoles.add(verified);

        toGiveRoles.add(registered);

        // Give and take the provided roles
        guild.modifyMemberRoles(target, toGiveRoles, toTakeRoles).queue();

        logRegister(target, toGiveRoles, toTakeRoles, author);

        event.reply("Você registrou com sucesso <@" + target.getIdLong() + ">.").setEphemeral(true).queue();
        deleteLastMessageByUser(target, event.getChannel());
    }

    private boolean isInputExact(Message message) {
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String ageInput = args[0].substring(3, args[0].length()-1);
        List<String> ages = List.of("-13", "-18", "+18");

        return ages.contains(ageInput);
    }

    private boolean isInputValid(Message message) {
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

    private boolean rolesExist(Guild guild) {
        requiredRole = guild.getRoleById(RegistrationRoles.ROLE_REQUIRED.toId());

        notRegistered = guild.getRoleById(RegistrationRoles.ROLE_NOT_REGISTERED.toId());
        registered = guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.toId());
        verified = guild.getRoleById(RegistrationRoles.ROLE_VERIFIED.toId());

        male = guild.getRoleById(RegistrationRoles.ROLE_MALE.toId());
        female = guild.getRoleById(RegistrationRoles.ROLE_FEMALE.toId());
        nonBinary = guild.getRoleById(RegistrationRoles.ROLE_NON_BINARY.toId());

        adult = guild.getRoleById(RegistrationRoles.ROLE_ADULT.toId());
        underage = guild.getRoleById(RegistrationRoles.ROLE_UNDERAGE.toId());
        under13 = guild.getRoleById(RegistrationRoles.ROLE_UNDER13.toId());

        pc = guild.getRoleById(RegistrationRoles.ROLE_COMPUTER.toId());
        mobile = guild.getRoleById(RegistrationRoles.ROLE_MOBILE.toId());

        // Were all roles found?
        RegistrationRoles[] roles = RegistrationRoles.values();

        for (RegistrationRoles i : roles)
            if (guild.getRoleById(i.toId()) == null) return false;

        return true;
    }

    private void areRolesSetupProperly(Guild guild) {
        RegistrationRoles[] roles = RegistrationRoles.values();

        for (RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.toId());
            Role selfHighest = guild.getSelfMember().getRoles().get(0);

            if (targetRole == null)
                throw new IllegalArgumentException("Role `RegistrationRoles." + r.name() + "` cannot be null");

            if (targetRole.getPosition() > selfHighest.getPosition() || !guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES))
                throw new HierarchyException(targetRole.getName() + " has a higher position than my highest role or I don't have Permission.MANAGE_ROLES enabled");
        }
    }

    private void logRegister(Member target, List<Role> givenRoles, List<Role> removedRoles, User author) {
        EmbedBuilder builder = new EmbedBuilder();
        String targetName = target.getUser().getName();
        String targetDiscriminator = target.getUser().getDiscriminator();
        String staffName = author.getName();
        String staffDiscriminator = author.getDiscriminator();
        Guild guild = target.getGuild();
        TextChannel channel = target.getGuild().getTextChannelById(Channels.REGISTER_LOG_CHANNEL.toId());

        builder
                .setColor(Color.GREEN)
                .setThumbnail(target.getEffectiveAvatarUrl())
                .setTitle("`" + targetName + "#" + targetDiscriminator + "` foi registrado!")
                .setDescription("Registrado por `" + staffName + "#" + staffDiscriminator + "`")
                .addField("> **Cargos Dados**", formattedRolesToEmbed(givenRoles), true)
                .addField("> **Cargos Removidos**", formattedRolesToEmbed(removedRoles), true)
                .setFooter("Oficina Myuu・ID: " + target.getIdLong(), guild.getIconUrl());

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        else System.out.println("Não foi possível salvar o registro pois nenhum chat foi encontrado.");

        System.out.println(
                getLog("""
                        \s
                        <author> registrou o membro <target>
                        
                        Cargos:
                        Gênero: <gender>
                        Idade: <age>
                        Plataforma: <plataform>
                        """, author, target, givenRoles)
        );
    }

    private String getLog(String log, User author, Member target, List<Role> givenRoles) {
        final HashMap<String, String> placeholders = new HashMap<>();
        User targetUser = target.getUser();

        placeholders.put("<author>", author.getName() + "#" + author.getDiscriminator());
        placeholders.put("<target>", targetUser.getName() + "#" + targetUser.getDiscriminator());

        // Gender input
        if (givenRoles.get(0).getIdLong() == RegistrationRoles.ROLE_FEMALE.toId()) placeholders.put("<gender>", "Feminino");
        if (givenRoles.get(0).getIdLong() == RegistrationRoles.ROLE_MALE.toId()) placeholders.put("<gender>", "Masculino");
        if (givenRoles.get(0).getIdLong() == RegistrationRoles.ROLE_NON_BINARY.toId()) placeholders.put("<gender>", "Não binário");

        // Age input
        if (givenRoles.get(1).getIdLong() == RegistrationRoles.ROLE_ADULT.toId()) placeholders.put("<age>", "Maior de idade");
        if (givenRoles.get(1).getIdLong() == RegistrationRoles.ROLE_UNDERAGE.toId()) placeholders.put("<age>", "Menor de idade");
        if (givenRoles.get(1).getIdLong() == RegistrationRoles.ROLE_UNDER13.toId()) placeholders.put("<age>", "Menor de idade + (😻)");

        // Plataform input
        if (givenRoles.get(givenRoles.size()-2).getIdLong() == RegistrationRoles.ROLE_MOBILE.toId()) placeholders.put("<plataform>", "Mobile");
        if (givenRoles.get(givenRoles.size()-2).getIdLong() == RegistrationRoles.ROLE_COMPUTER.toId()) placeholders.put("<plataform>", "Computador");

        for (String i : placeholders.keySet())
            log = log.replaceAll(i, placeholders.get(i));

        return log;
    }

    private String formattedRolesToEmbed(List<Role> roles) {
        StringBuilder builder = new StringBuilder();

        for (Role r : roles) {
            builder.append("<@&")
                    .append(r.getIdLong())
                    .append(">\n");
        }

        return builder.toString().stripTrailing();
    }

    private void deleteLastMessageByUser(Member target, MessageChannelUnion channel) {
        List<Message> history = channel.asTextChannel().getHistory().retrievePast(20).complete();

        for (Message m : history) {
            if (m.getAuthor().getIdLong() != target.getUser().getIdLong()) continue;

            m.delete().queue();
            break;
        }
    }
}