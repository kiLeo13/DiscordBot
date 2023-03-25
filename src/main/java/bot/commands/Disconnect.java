package bot.commands;

import bot.util.Channels;
import bot.util.Extra;
import bot.util.Messages;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class Disconnect {
    private Disconnect() {}

    public static void run(Message message) {
        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_CHANNELS;
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
            Extra.sendExpireMessage(channel,
                    "<@" + member.getIdLong() + "> " + Messages.ERROR_CHANNEL_NOT_FOUND.toMessage(),
                    10000);
            return;
        }

        channel.sendMessage("<@" + member.getId() + "> ok, desconectado :)").queue();
        message.delete().queue();
    }

    public static void run(SlashCommandInteractionEvent e) {

        List<Long> allowedDisconnectChannels = Channels.COMMAND_DISCONNECT_CHANNELS;
        if (!allowedDisconnectChannels.contains(e.getChannel().getIdLong())) return;

        Guild guild = e.getGuild();
        Member member = e.getMember();

        if (guild == null || member == null) return;

        try {
            guild.kickVoiceMember(member).queue();
        } catch (IllegalStateException exception) {
            e.reply(Messages.ERROR_CHANNEL_NOT_FOUND.toMessage())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        e.reply("Ok, desconectado :)")
                .setEphemeral(true)
                .queue();
    }
}