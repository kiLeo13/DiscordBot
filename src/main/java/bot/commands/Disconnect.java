package bot.commands;

import bot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;

public class Disconnect implements CommandExecutor, SlashExecutor {

    @Override
    public void help(Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        List<Long> requiredChannel = Channels.COMMAND_DISCONNECT_CHANNELS.toIds();
        StringBuilder channelsName = new StringBuilder();
        String channels;

        requiredChannel.forEach(c -> {
            TextChannel textChannel = guild.getTextChannelById(c);

            if (textChannel != null) channelsName.append("`").append(textChannel.getName()).append("`\n");
        });

        channels = channelsName.toString().stripTrailing();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Disconnect", guild.getIconUrl())
                .setDescription("Segue uma explicação mais detalhada sobre o comando fornecido.")
                .addField("> 📝 Requisitos", "Para executar este comando, requer estar nos canais:\n" + channels, true)
                .addField("> ❓ O que é", "Desenvolvido para desconectar o usuário de um canal de voz caso esteja lagados e não consigam sair naturalmente.", true)
                .addField("> ❗ Disclaimer", "Este comando não irá funcionar marcando outras pessoas independente de qualquer permissão.", true)
                .addField("> Syntax: `.disconnect`", "Ex: `.disconnect`.", false)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public void run(Message message) {
        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_CHANNELS.toIds();
        if (allowedDisconnectChannels.isEmpty()) return;

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!allowedDisconnectChannels.contains(channel.getIdLong())) return;
        if (member == null) return;

        try {
            guild.kickVoiceMember(member).queue();
        } catch (IllegalStateException exception) {
            message.delete().queue();
            BotSystem.sendExpireMessage(channel,
                    "<@" + member.getIdLong() + "> " + Messages.ERROR_CHANNEL_NOT_FOUND.message(),
                    10000);
            return;
        }

        channel.sendMessage("<@" + member.getId() + "> ok, desconectado :)").queue();
        message.delete().queue();
    }

    @Override
    public void runAsSlash(SlashCommandInteractionEvent e) {

        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_CHANNELS.toIds();
        if (!allowedDisconnectChannels.contains(e.getChannel().getIdLong())) return;

        Guild guild = e.getGuild();
        Member member = e.getMember();

        if (guild == null || member == null) return;

        try {
            guild.kickVoiceMember(member).queue();
        } catch (IllegalStateException exception) {
            e.reply(Messages.ERROR_CHANNEL_NOT_FOUND.message())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        e.reply("Ok, desconectado :)")
                .setEphemeral(true)
                .queue();
    }
}