package bot.commands;

import bot.util.Channels;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

import static bot.util.Extra.sendExpireMessage;

public class Disconnect {
    private Disconnect() {}

    public static void run(Message message) {
        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_CHANNELS;
        if (allowedDisconnectChannels.isEmpty()) return;

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String messageId = message.getId();

        if (member == null) return;
        if (!allowedDisconnectChannels.contains(channel.getIdLong())) return;

        GuildVoiceState voiceState = member.getVoiceState();
        Guild guild = member.getGuild();

        Channel voiceChannel;
        String voiceChannelName;

        try {
            if (voiceState == null) throw new NullPointerException("Voicestate cannot be null in this command");
            voiceChannel = voiceState.getChannel();

            if (voiceChannel == null) throw new NullPointerException("Voicechannel cannot be null in this comand");
            voiceChannelName = voiceChannel.getName();
        } catch (NullPointerException error) {
            message.delete().queue();
            sendExpireMessage(channel, "<@" + member.getId() + "> channel not found, are you sure you are connected to one?", 5000);
            return;
        }

        channel.sendMessage("<@" + member.getId() + "> okay, disconnected :)").queue();
        guild.kickVoiceMember(member).queue();
        System.out.println(
                "Desconectamos " +
                        member.getEffectiveName() +
                        "#"+
                        member.getUser().getDiscriminator() +
                        "\nDo canal: #" + voiceChannelName);

        channel.deleteMessageById(messageId).queue();
    }
}