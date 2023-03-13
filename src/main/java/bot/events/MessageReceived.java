package bot.events;

import bot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        User author = event.getAuthor();

        // Disconnect command
        if (message.equals(".DISCONNECT") && !author.isBot()) disconnectCommand(event);

        // Check if message has prefix (r!)
        if (message.equalsIgnoreCase(".ping")) pingComand(event);

        // Swearing command
        if (message.equalsIgnoreCase(".puta")) swearCommand(event);
    }

    private void registerCommand(MessageReceivedEvent e) {

        Member member = e.getMember();

        if (member == null) return;

        Role role = member.getJDA().getRoleById("1009140499325648991");
        MessageChannelUnion channel = e.getChannel();

        if (role == null) {
            channel.sendMessage("Required role was not found.").queue();
            return;
        }

        if (member.getRoles().contains(role)) {
            channel.sendMessage("É, <@" + member.getId() + "> me parece que vc tem o cargo <@&" + role.getId() + "> :medo:").queue();
        }
    }

    private void disconnectCommand(MessageReceivedEvent e) {

        MessageChannelUnion channel = e.getChannel();
        Member member = e.getMember();
        String messageId = e.getMessageId();

        if (!channel.getId().equals("1084341941984034816")) return;
        if (member == null) return;

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

    private void pingComand(MessageReceivedEvent e) {

        User author = e.getAuthor();
        MessageChannelUnion channel = e.getChannel();
        JDA api = Main.getApi();

        if (author.isBot()) return;

        channel.sendMessage("Oioioioioioi\nGateway ping: `" + api.getGatewayPing() + "ms`").queue();
    }

    private void swearCommand(MessageReceivedEvent e) {

        Member member = e.getMember();
        List<String> swearingList = Main.getFileObject().get("swearings");

        if (member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        int random = (int) Math.floor(Math.random() * swearingList.size());

        e.getMessage().reply(swearingList.get(random)).queue();
    }
}