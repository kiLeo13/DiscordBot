package bot.commands;

import bot.util.content.Channels;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

@CommandPermission(permissions = Permission.ADMINISTRATOR)
public class RegistrationRoles implements CommandExecutor {

    @Override
    public void run(Message message) {

        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        MessageCreateBuilder send = new MessageCreateBuilder();

        if (channel.getId().equals(Channels.REGISTER_CHANNEL.id())) return;

        send.setEmbeds(embed(guild));
        send.setContent("<@" + author.getId() + ">");

        channel.sendMessage(send.build()).queue();
    }

    private MessageEmbed embed(Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("Registration Roles:")
                .setColor(Color.GREEN)
                .setDescription("Aqui estão os ENUMS dos cargos e seus nomes.")
                .setThumbnail("https://cdn.discordapp.com/attachments/631974560605929493/1086539928596398110/image.png")
                .setFooter("Oficina Myuu", "https://cdn.discordapp.com/attachments/631974560605929493/1086540588788228117/a_d51df27b11a16bbfaf5ce83acfeebfd8.png");

        bot.util.content.RegistrationRoles[] roles = bot.util.content.RegistrationRoles.values();

        for (bot.util.content.RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.id());

            if (targetRole == null) builder.addField("> `" + r.name() + "`", "`⚠ Not Found`", false);
            else builder.addField("> `" + r.name() + "`", "<@&" + targetRole.getIdLong() + ">", false);
        }

        return builder.build();
    }
}