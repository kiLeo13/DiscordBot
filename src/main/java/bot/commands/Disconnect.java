package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

@CommandPermission()
public class Disconnect extends BotCommand {

    public Disconnect(String... names) {
        super(true, names);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!Channels.COMMAND_DISCONNECT_SELF_CHANNELS.ids().contains(channel.getId())) return;

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            Bot.tempMessage(channel, Messages.ERROR_VOICE_CHANNEL_NOT_FOUND.message(), 10000);
            return;
        }

        guild.kickVoiceMember(member).queue(s -> {
            Bot.tempMessage(channel, "Ok, desconectado :)", 10000);
        }, e -> {
            e.printStackTrace();
            Bot.tempMessage(channel, "Não foi possível desconectar.", 10000);
        });
    }
}