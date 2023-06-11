package bot.commands;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import bot.util.content.RegistrationRoles;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@CommandPermission(permissions = Permission.MANAGE_ROLES)
public class RegistrationTake implements CommandExecutor {

    @Override
    public void run(@NotNull Message message) {

        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        TextChannel channel = message.getChannel().asTextChannel();

        if (channel.getId().equals(Channels.REGISTER_CHANNEL.id())) return;

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        Bot.fetchMember(guild, args[1]).queue(m -> {
            List<Role> toGive = toGive(guild);
            List<Role> toRemove = toRemove(m);

            if (m.getRoles().contains(guild.getRoleById(RegistrationRoles.ROLE_NOT_REGISTERED.id()))
                    && !m.getRoles().contains(guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.id()))) {
                channel.sendMessage("O membro <@" + m.getId() + "> não está registrado.").queue();
                return;
            }

            guild.modifyMemberRoles(m, toGive, toRemove).queue(s -> {
                channel.sendMessage("O registro de <@" + m.getId() + "> foi removido com sucesso!").queue();
                logRegister(m, toGive, toRemove, member);
            }, e -> {
                channel.sendMessage("Não foi possível concluir a operação! Verifique o console para mais informações sobre o erro.").queue();
                e.printStackTrace();
            });

        }, e -> Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000));
    }

    private void logRegister(Member target, List<Role> givenRoles, List<Role> removedRoles, Member staff) {
        EmbedBuilder builder = new EmbedBuilder();
        TextChannel channel = target.getGuild().getTextChannelById(Channels.REGISTER_LOG_CHANNEL.id());

        builder
                .setColor(Color.RED)
                .setThumbnail(target.getEffectiveAvatarUrl())
                .setTitle("Registro de `" + target.getUser().getName() + "` removido!")
                .setDescription("Removido por `" + staff.getUser().getName() + "`\n ")
                .addField("> **Cargos Dados**", getFormattedRolesToEmbed(givenRoles), true)
                .addField("> **Cargos Removidos**", getFormattedRolesToEmbed(removedRoles), true)
                .setFooter("Oficina Myuu", "https://cdn.discordapp.com/attachments/631974560605929493/1086540588788228117/a_d51df27b11a16bbfaf5ce83acfeebfd8.png");

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        else System.out.println("Não foi possível salvar o registro pois nenhum chat foi encontrado.");
    }

    private String getFormattedRolesToEmbed(List<Role> roles) {
        StringBuilder builder = new StringBuilder();

        for (Role r : roles) {
            builder.append("<@&")
                    .append(r.getIdLong())
                    .append(">\n");
        }

        return builder.toString().stripTrailing();
    }

    private List<Role> toRemove(Member target) {
        Guild guild = target.getGuild();
        RegistrationRoles[] roles = RegistrationRoles.values();
        List<Role> finalRoles = new ArrayList<>();

        for (RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.id());

            if (r.emoji().equals("✅") && target.getRoles().contains(targetRole))
                finalRoles.add(targetRole);
        }

        return finalRoles;
    }

    private List<Role> toGive(Guild guild) {
        RegistrationRoles[] roles = RegistrationRoles.values();
        List<Role> finalRoles = new ArrayList<>();

        for (RegistrationRoles r : roles)
            if (r.emoji().equals("❌")) finalRoles.add(guild.getRoleById(r.id()));

        return finalRoles;
    }
}