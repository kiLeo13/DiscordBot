package bot.commands;

import bot.util.*;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationTake implements CommandExecutor {

    @Override
    public void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        Member target;

        if (channel.getIdLong() == Channels.REGISTER_CHANNEL.toId()) return;
        if (member == null || !member.hasPermission(Permission.MANAGE_ROLES)) return;

        try {
            String targetRegex = args[1].replaceAll("[^0-9]+", "");
            target = guild.retrieveMemberById(targetRegex).complete();
        } catch (IndexOutOfBoundsException e) { target = null; }

        if (target == null) {
            BotSystem.sendExpireMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
            message.delete().queue();
            return;
        }

        List<Role> toRemoveRoles = toRemove(target);
        List<Role> toGiveRoles = toGive(guild);

        if (target.getRoles().contains(guild.getRoleById(RegistrationRoles.ROLE_NOT_REGISTERED.toId()))
                && !target.getRoles().contains(guild.getRoleById(RegistrationRoles.ROLE_REGISTERED.toId()))) {
            channel.sendMessage("<@" + author.getIdLong() + "> o membro <@" + target.getIdLong() + "> não está registrado.").queue();
            message.delete().queue();
            return;
        }

        guild.modifyMemberRoles(target, toGiveRoles, toRemoveRoles).queue();
        message.delete().queue();
        channel.sendMessage("<@" + author.getIdLong() + "> registro de <@" + target.getIdLong() + "> foi removido com sucesso!").queue();
        logRegister(target, toGiveRoles, toRemoveRoles, member);
    }

    private static void logRegister(Member target, List<Role> givenRoles, List<Role> removedRoles, Member registerMaker) {
        EmbedBuilder builder = new EmbedBuilder();
        String targetName = target.getUser().getName();
        String targetDiscriminator = target.getUser().getDiscriminator();
        String staffName = registerMaker.getUser().getName();
        String staffDiscriminator = registerMaker.getUser().getDiscriminator();
        TextChannel channel = target.getGuild().getTextChannelById(Channels.REGISTER_LOG_CHANNEL.toId());

        builder
                .setColor(Color.RED)
                .setThumbnail(target.getEffectiveAvatarUrl())
                .setTitle("Registro de `" + targetName + "#" + targetDiscriminator + "` removido!")
                .setDescription("Removido por `" + staffName + "#" + staffDiscriminator + "`\n ")
                .addField("> **Cargos Dados**", getFormattedRolesToEmbed(givenRoles) + "", true)
                .addField("> **Cargos Removidos**", getFormattedRolesToEmbed(removedRoles), true)
                .setFooter("Oficina Myuu", "https://cdn.discordapp.com/attachments/631974560605929493/1086540588788228117/a_d51df27b11a16bbfaf5ce83acfeebfd8.png");

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
        else System.out.println("Não foi possível salvar o registro pois nenhum chat foi encontrado.");
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

    private static List<Role> toRemove(Member target) {
        Guild guild = target.getGuild();
        RegistrationRoles[] roles = RegistrationRoles.values();
        List<Role> finalRoles = new ArrayList<>();

        for (RegistrationRoles r : roles) {
            Role targetRole = guild.getRoleById(r.toId());

            if (r.emoji().equals("✅") && target.getRoles().contains(targetRole))
                finalRoles.add(targetRole);
        }

        return finalRoles;
    }

    private static List<Role> toGive(Guild guild) {
        RegistrationRoles[] roles = RegistrationRoles.values();
        List<Role> finalRoles = new ArrayList<>();

        for (RegistrationRoles r : roles)
            if (r.emoji().equals("❌")) finalRoles.add(guild.getRoleById(r.toId()));

        return finalRoles;
    }
}