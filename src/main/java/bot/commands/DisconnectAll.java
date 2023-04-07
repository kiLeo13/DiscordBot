package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class DisconnectAll implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        String[] args = content.split(" ");

        if (args.length < 2) {
            Bot.sendExpireMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 5000);
            return;
        }

        String channelRegex = args[1].replaceAll("[^0-9]+", "");
        VoiceChannel voiceChannel = guild.getVoiceChannelById(channelRegex);

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        if (voiceChannel == null) {
            Bot.sendExpireMessage(channel, Messages.ERROR_VOICE_CHANNEL_NOT_FOUND.message(), 10000);
            message.delete().queue();
            return;
        }

        if (voiceChannel.getMembers().size() == 0) {
            Bot.sendExpireMessage(channel, Messages.ERROR_VOICE_CHANNEL_EMPTY.message(), 5000);
            message.delete().queue();
            return;
        }

        // Always disconnect every user
        disconnect(voiceChannel, null, guild);

        Bot.sendExpireMessage(channel, "Todos os membros de `#" + voiceChannel.getName() + "` foram desconectados.", 10000);
        message.delete().queue();
    }

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        OptionMapping option = event.getOption("filter");

        event.reply("Comando em estágio de desenvolvimento...").setEphemeral(true).queue();
    }

    private void disconnect(VoiceChannel voiceChannel, long filter, Guild guild) {
        Role role = guild.getRoleById(filter);

        if (role == null) throw new IllegalArgumentException("Filter role cannot be null");

        voiceChannel.getMembers().forEach(member -> {
            if (!member.getRoles().contains(role)) guild.kickVoiceMember(member).queue();
        });
    }

    private void disconnect(VoiceChannel voiceChannel, List<Long> filters, Guild guild) {
        if (filters == null) {
            voiceChannel.getMembers().forEach(member -> guild.kickVoiceMember(member).queue());
            return;
        }

        filters.forEach(role -> {
            Role filterRole = guild.getRoleById(role);

            voiceChannel.getMembers().forEach(member -> {
                if (!member.getRoles().contains(filterRole)) guild.kickVoiceMember(member).queue();
            });
        });
    }
}