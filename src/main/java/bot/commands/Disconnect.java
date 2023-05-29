package bot.commands;

import bot.util.*;
import bot.util.annotations.CommandPermission;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

@CommandPermission()
public class Disconnect implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!Channels.COMMAND_DISCONNECT_SELF_CHANNELS.ids().contains(channel.getIdLong())) return;

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            Bot.tempMessage(channel, Messages.ERROR_VOICE_CHANNEL_NOT_FOUND.message(), 10000);
            return;
        }

        guild.kickVoiceMember(member).queue();
        Bot.tempMessage(channel, "Ok, desconectado :)", 10000);
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
}