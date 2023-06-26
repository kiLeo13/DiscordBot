package bot.commands;

import bot.internal.abstractions.BotCommand;
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

public class RegistrationTake extends BotCommand {

    public RegistrationTake(String name) {
        super(true, 1, Permission.MANAGE_ROLES, "{cmd} <member>", name);
    }

    @Override
    public void run(Message message, String[] args) {

        Member member = message.getMember();
        Guild guild = message.getGuild();
        TextChannel channel = message.getChannel().asTextChannel();

        if (channel.getId().equals(Channels.REGISTER_CHANNEL.id())) return;

        Bot.fetchMember(guild, args[0]).queue(m -> {
            List<Role> toGive = toGive(guild);
            List<Role> toRemove = toRemove(m);

            if (m.getRoles().contains(guild.getRoleById(RegistrationRoles.NOT_REGISTERED.id()))
                    && !m.getRoles().contains(guild.getRoleById(RegistrationRoles.REGISTERED.id()))) {
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

        }, e -> Bot.tempEmbed(channel, Responses.ERROR_MEMBER_NOT_FOUND, 10000));
    }

    private void logRegister(Member target, List<Role> givenRoles, List<Role> removedRoles, Member staff) {
        EmbedBuilder builder = new EmbedBuilder();
        TextChannel channel = target.getGuild().getTextChannelById(Channels.REGISTER_LOG_CHANNEL.id());

        builder
                .setColor(Color.RED)
                .setThumbnail(target.getUser().getAvatarUrl())
                .setTitle("Registro de `" + target.getUser().getName() + "` removido!")
                .setDescription("Removido por `" + staff.getUser().getName() + "`\n ")
                .addField("> **Cargos Dados**", formatRoles(givenRoles), true)
                .addField("> **Cargos Removidos**", formatRoles(removedRoles), true)
                .setFooter("Oficina Myuu", "https://cdn.discordapp.com/attachments/631974560605929493/1086540588788228117/a_d51df27b11a16bbfaf5ce83acfeebfd8.png");

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
    }

    private String formatRoles(List<Role> roles) {
        StringBuilder builder = new StringBuilder();

        for (Role r : roles) {
            builder.append(r.getAsMention())
                    .append("\n");
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