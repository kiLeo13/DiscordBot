package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import bot.util.content.RegistrationRoles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@CommandPermission()
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
        Member member = message.getMember();
        Guild guild = message.getGuild();
        Pattern pattern = Pattern.compile("^[fmn][0-9][pm]$");

        if (!checkRoles(guild)) {
            Bot.log("Not all roles were found to complete the registration process or one of them is higher than me! Aborting...", true);
            return;
        }

        // Members with Manage Roles are allowed to run this command anywhere
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !channel.getId().equals(Channels.REGISTER_CHANNEL.id())) return;

        // Members with Manage Roles are not required to have the required role
        if (!member.hasPermission(Permission.MANAGE_ROLES) && !member.getRoles().contains(requiredRole)) return;

        if (args.length < 1) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        if (pattern.matcher(content.split(" ")[0]).matches()) {
            Bot.tempMessage(channel, "O padrão de registro usado é inválido.", 10000);
            return;
        }

        Bot.fetchMember(guild, args[0]).queue(target -> {
            final List<Role> rolesAdd = resolveRoles(content);
            final List<Role> rolesRemove = new ArrayList<>(2);

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

            guild.modifyMemberRoles(target, rolesAdd, rolesRemove)
                    .queue(s -> Bot.tempMessage(channel, "Membro " + target.getAsMention() + " foi registrado(a) com sucesso!", 10000),
                            e -> {
                                Bot.tempMessage(channel, "Algo deu errado. Verifique o console para mais informações sobre o erro.", 10000);
                                e.printStackTrace();
                            });
        }, e -> Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000));
    }

    private List<Role> resolveRoles(String arg) {
        final List<Role> roles = new ArrayList<>(4);

        roles.add(resolveGender(arg));
        roles.addAll(resolveAge(arg));
        roles.add(resolvePlataform(arg));

        return roles;
    }

    private Role resolveGender(String arg) {
        char value = arg.charAt(0);

        switch (value) {
            case 'f' -> { return female; }
            case 'm' -> { return male; }
            case 'n' -> { return nonBinary; }
        }

        return null;
    }

    private List<Role> resolveAge(String arg) {
        String value = arg.substring(1, arg.length() - 2);
        final List<Role> roles = new ArrayList<>(2);

        try {
            byte age = Byte.parseByte(value);

            if (age < 13) roles.add(under13);
            if (age < 18) roles.add(underage);
            if (age > 17) roles.add(adult);

            return roles;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Role resolvePlataform(String arg) {
        char value = arg.charAt(arg.length() - 1);

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
}