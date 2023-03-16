package bot.events;

import bot.Main;
import bot.commands.Ping;
import bot.util.Channels;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        MessageChannelUnion channel = event.getChannel();
        User author = event.getAuthor();

        // Disconnect command
        if (message.toLowerCase().startsWith(".disconnect")) disconnectCommand(event);

        if (message.toLowerCase().startsWith(".ping")) Ping.run(channel, author);

        // Swearing command
        if (message.toLowerCase().startsWith(".puta")) swearCommand(event);
    }

    private void disconnectCommand(MessageReceivedEvent e) {

        List<Long> allowedDisconnectChannels = Channels.DISCONNECT_CHANNELS.get();
        if (allowedDisconnectChannels.isEmpty()) return;

        MessageChannelUnion channel = e.getChannel();
        Member member = e.getMember();
        String messageId = e.getMessageId();

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
            channel.sendMessage("<@" + member.getId() + "> channel not found, are you sure you are connected to one?").queue();
            return;
        }

        channel.sendMessage("<@" + member.getId() + "> okay, disconnected :)").queue();
        guild.kickVoiceMember(member).queue();
        System.out.println(
                "Disconneted " +
                        member.getEffectiveName() +
                        "#"+
                        member.getUser().getDiscriminator() +
                        "\nFrom channel: " + voiceChannelName);

        channel.deleteMessageById(messageId).queue();
    }

    private void swearCommand(MessageReceivedEvent e) {

        List<Long> allowedSwearingChannels = Channels.SWEARING_CHANNELS.get();
        if (allowedSwearingChannels.isEmpty()) return;

        Member mentionedMember = null;
        String[] args = e.getMessage().getContentRaw().split(" ");
        MessageChannelUnion channel = e.getChannel();
        Member member = e.getMember();
        List<String> swearingList = Main.getSwearings().get("swearings");
        Message message = e.getMessage();

        if (!allowedSwearingChannels.contains(channel.getIdLong())) return;
        if (args.length >= 2) mentionedMember =  e.getMessage().getMentions().getMembers().get(0);

        if (member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        int random = (int) Math.floor(Math.random() * swearingList.size());

        if (mentionedMember == null) e.getMessage().reply(swearingList.get(random)).queue();
        else {
            channel.sendMessage("<@" + mentionedMember.getId() + "> " + swearingList.get(random)).queue();
            message.delete().queue();
        }
    }
}