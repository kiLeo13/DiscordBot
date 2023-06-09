package bot.commands;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Responses;
import bot.util.content.RegistrationRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Register { // Register is a special command, we don't use the abstract class here
    private static Register instance;
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

    private Register() {}

    public static Register getInstance() {
        if (instance == null) instance = new Register();
        return instance;
    }

    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        String content = message.getContentRaw().substring(2);
        String regex = "^[fmn][0-9]+[pm]$";
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!checkRoles(guild)) {
            Bot.log("{YELLOW}Roles not found to complete the registration process or one of them is higher than me! Aborting...");
            return;
        }

        // Members with Manage Roles are allowed to run this command anywhere
        if (!member.hasPermission(Permission.MANAGE_ROLES) && channel.getIdLong() != Channels.REGISTER_CHANNEL.id()) return;

        // Members with Manage Roles are not required to have the required role
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !member.getRoles().contains(requiredRole)) return;

        if (args.length < 2) {
            Bot.tempEmbed(channel, Responses.ERROR_TOO_FEW_ARGUMENTS, 10000);
            return;
        }

        if (!Pattern.matches(regex, args[0])) {
            Bot.tempMessage(channel, "O padrão de registro usado é inválido.", 10000);
            return;
        }

        Bot.fetchMember(guild, args[1]).queue(target -> {
            final List<Role> rolesAdd = resolveRoles(args[0]);
            final List<Role> rolesRemove = new ArrayList<>(2);

            // This is a generic error that will probably never be reached
            if (rolesAdd == null) {
                Bot.tempMessage(channel, "Não foi possível completar a operação.", 10000);
                return;
            }

            if (target.getRoles().contains(registered)) {
                Bot.tempMessage(channel, "O membro `" + target.getUser().getName() + "` já está registrado.", 10000);
                return;
            }

            if (target.getUser().isBot()) {
                Bot.tempMessage(channel, "Você não pode registrar um bot.", 10000);
                return;
            }

            if (rolesAdd.contains(null)) {
                Bot.tempMessage(channel, "Nenhum registro enconstrado para o padrão `" + content + "` fornecido.", 10000);
                return;
            }

            if (target.getRoles().contains(notRegistered))
                rolesRemove.add(notRegistered);

            if (target.getRoles().contains(verified))
                rolesRemove.add(verified);

            guild.modifyMemberRoles(target, rolesAdd, rolesRemove).queue(s -> {
                Bot.tempMessage(channel, member.getAsMention() + " Membro " + target.getAsMention() + " foi registrado com sucesso!", 10000);
                log(target, member, rolesAdd, rolesRemove);
                deleteLastMessageByUser(target, channel);
            }, e -> {
                Bot.tempMessage(channel, "Algo deu errado. Verifique o console para mais informações sobre o erro.", 10000);
                e.printStackTrace();
            });
        }, e -> Bot.tempEmbed(channel, Responses.ERROR_MEMBER_NOT_FOUND, 10000));
    }

    private List<Role> resolveRoles(String input) {
        final List<Role> roles = new ArrayList<>();

        roles.add(resolveGender(input));
        roles.addAll(resolveAge(input));
        roles.add(resolvePlataform(input));

        if (roles.size() < 3)
            return null;

        roles.add(registered);

        return roles;
    }

    private Role resolveGender(String input) {
        char value = input.charAt(0);

        switch (value) {
            case 'f' -> { return female; }
            case 'm' -> { return male; }
            case 'n' -> { return nonBinary; }
        }

        return null;
    }

    private List<Role> resolveAge(String input) {
        String value = input.substring(1, input.length() - 1);
        final List<Role> roles = new ArrayList<>(2);

        try {
            short age = Short.parseShort(value);

            if (age < 13) roles.add(under13);
            if (age < 18) roles.add(underage);
            if (age > 17) roles.add(adult);

            return roles;
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    private Role resolvePlataform(String input) {
        char value = input.charAt(input.length() - 1);

        switch (value) {
            case 'm' -> { return mobile; }
            case 'p' -> { return pc; }
        }

        return null;
    }

    // Better safe than sorry?
    private boolean checkRoles(Guild guild) {
        final Role highest = guild.getSelfMember().getRoles().get(0);

        requiredRole = guild.getRoleById(RegistrationRoles.REQUIRED.id());

        // Final Register
        notRegistered = guild.getRoleById(RegistrationRoles.NOT_REGISTERED.id());
        registered = guild.getRoleById(RegistrationRoles.REGISTERED.id());
        verified = guild.getRoleById(RegistrationRoles.VERIFIED.id());

        // Gender
        male = guild.getRoleById(RegistrationRoles.MALE.id());
        female = guild.getRoleById(RegistrationRoles.FEMALE.id());
        nonBinary = guild.getRoleById(RegistrationRoles.NON_BINARY.id());

        // Age
        adult = guild.getRoleById(RegistrationRoles.ADULT.id());
        underage = guild.getRoleById(RegistrationRoles.UNDERAGE.id());
        under13 = guild.getRoleById(RegistrationRoles.UNDER13.id());

        // Plataform
        pc = guild.getRoleById(RegistrationRoles.PC.id());
        mobile = guild.getRoleById(RegistrationRoles.MOBILE.id());

        for (RegistrationRoles rr : RegistrationRoles.values()) {
            Role role = guild.getRoleById(rr.id());

            if (role == null || role.getPosition() > highest.getPosition())
                return false;
        }

        return true;
    }

    private void log(Member member, Member moderator, List<Role> added, List<Role> taken) {
        final EmbedBuilder builder = new EmbedBuilder();
        Guild guild = member.getGuild();
        TextChannel log = guild.getTextChannelById(Channels.REGISTER_LOG_CHANNEL.id());

        if (log == null) {
            Bot.log("{RED}Register log channel not found! Ignoring it.");
            return;
        }

        builder
                .setTitle(String.format("`%s` foi registrado!", member.getUser().getEffectiveName()))
                .setThumbnail(member.getUser().getAvatarUrl())
                .setDescription(String.format("Registrado por `%s`.", moderator.getUser().getEffectiveName()))
                .addField("Adicionados", format(added), true)
                .addField("Removidos", format(taken), true)
                .setColor(Color.GREEN)
                .setFooter(guild.getName() + "・ID: " + member.getId(), guild.getIconUrl());

        log.sendMessageEmbeds(builder.build()).queue();

        String out = String.format("%s registrou o membro %s!", moderator.getUser().getEffectiveName(), member.getUser().getEffectiveName());
        Bot.log(out);
    }

    private String format(List<Role> roles) {
        final StringBuilder builder = new StringBuilder();

        for (Role r : roles)
            builder
                    .append(r.getAsMention())
                    .append("\n");

        return builder.toString().stripTrailing();
    }

    private void deleteLastMessageByUser(Member target, TextChannel channel) {
        channel.getHistory().retrievePast(20).queue(history -> {
            for (Message m : history) {
                if (m.getAuthor().getIdLong() != target.getIdLong()) continue;

                m.delete().queue();
                break;
            }
        });
    }
}