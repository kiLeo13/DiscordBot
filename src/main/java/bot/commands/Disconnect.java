package bot.commands;

import bot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class Disconnect implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!Channels.COMMAND_DISCONNECT_SELF_CHANNELS.ids().contains(channel.getIdLong())) return;
        if (member == null) return;

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            Bot.sendGhostMessage(channel, Messages.ERROR_VOICE_CHANNEL_NOT_FOUND.message(), 10000);
            message.delete().queue();
            return;
        }

        guild.kickVoiceMember(member).queue();

        Bot.sendGhostMessage(channel, "Ok, desconectado :)", 10000);
        message.delete().queue();
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {

        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_SELF_CHANNELS.ids();
        if (!allowedDisconnectChannels.contains(event.getChannel().getIdLong())) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (guild == null || member == null) return;

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply(Messages.ERROR_VOICE_CHANNEL_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        guild.kickVoiceMember(member).queue();

        event.reply("Ok, desconectado :)")
                .setEphemeral(true)
                .queue();
    }

    @Override
    public MessageEmbed help(Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        return builder.build();
    }
}