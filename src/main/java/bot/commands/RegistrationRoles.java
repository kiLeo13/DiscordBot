package bot.commands;

import bot.util.Channels;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class RegistrationRoles {
    private static final EmbedBuilder embedBuilder = new EmbedBuilder();

    private RegistrationRoles() {}

    public static void run(Message message) {
        setupEmbed(message.getGuild());

        MessageEmbed messageEmbed = embedBuilder.build();
        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();

        if (!Channels.COMMAND_REGISTER_ROLES_CHECK_CHANNELS.toIds().contains(channel.getIdLong())) return;

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) return;

        Message embedSent;

        embedSent = channel.sendMessage("Obtendo informações...").complete();
        embedSent.editMessage("<@" + author.getIdLong() + "> Aqui estão!")
                .setEmbeds(messageEmbed)
                .queueAfter(500, TimeUnit.MILLISECONDS, null, new ErrorHandler()
                        .ignore(ErrorResponse.UNKNOWN_MESSAGE));

        message.delete().queue();
    }

    private static void setupEmbed(Guild guild) {
        embedBuilder.setTitle("Registration Roles:");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Aqui estão os ENUMS dos cargos e seus nomes.");
        embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/631974560605929493/1086539928596398110/image.png");
        embedBuilder.setFooter("Oficina Myuu", "https://cdn.discordapp.com/attachments/631974560605929493/1086540588788228117/a_d51df27b11a16bbfaf5ce83acfeebfd8.png");

        bot.util.RegistrationRoles[] roles = bot.util.RegistrationRoles.values();

        for (bot.util.RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.get());

            if (targetRole == null) embedBuilder.addField("> `" + r.name() + "`", "`⚠ Not Found`", false);
            else embedBuilder.addField("> `" + r.name() + "`", "<@&" + targetRole.getIdLong() + ">", false);
        }
    }
}