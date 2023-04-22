package bot.commands.register;

import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import org.jetbrains.annotations.NotNull;

public class Buttons extends ListenerAdapter {
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
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {

        /* []========[] In development stages... []========[]
        MessageChannelUnion channel = event.getChannel();
        Member member = event.getMember();
        User author = event.retrieveUser().complete();
        Guild guild = event.getGuild();
        String emoji = event.getReaction().getEmoji().getName();

        if (guild.getIdLong() != 624008072544780309L
                || channel.getIdLong() != 1098067105787687033L) return;

        channel.sendMessage(emoji).queue();
        System.out.println(emoji);

        if (!roles(guild)) {
            event.getReaction().removeReaction().queue();
            return;
        }

        if (member == null || author.isBot()) return;
        */
    }

    private boolean roles(Guild guild) {
        notRegistered = guild.getRoleById(RegistrationRoles.ROLE_NOT_REGISTERED.id());
        registered = guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.id());
        verified = guild.getRoleById(RegistrationRoles.ROLE_VERIFIED.id());

        male = guild.getRoleById(RegistrationRoles.ROLE_MALE.id());
        female = guild.getRoleById(RegistrationRoles.ROLE_FEMALE.id());
        nonBinary = guild.getRoleById(RegistrationRoles.ROLE_NON_BINARY.id());

        adult = guild.getRoleById(RegistrationRoles.ROLE_ADULT.id());
        underage = guild.getRoleById(RegistrationRoles.ROLE_UNDERAGE.id());
        under13 = guild.getRoleById(RegistrationRoles.ROLE_UNDER13.id());

        pc = guild.getRoleById(RegistrationRoles.ROLE_COMPUTER.id());
        mobile = guild.getRoleById(RegistrationRoles.ROLE_MOBILE.id());

        // Were all roles found?
        RegistrationRoles[] roles = RegistrationRoles.values();

        for (RegistrationRoles i : roles)
            if (guild.getRoleById(i.id()) == null) return false;

        return true;
    }
}