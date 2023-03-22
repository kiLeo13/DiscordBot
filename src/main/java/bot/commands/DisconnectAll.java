package bot.commands;

import bot.util.Extra;
import bot.util.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class DisconnectAll {
    private DisconnectAll() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        String[] args = content.split(" ");
        VoiceChannel voiceChannel;

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        try {
            String channelRegex = args[1].replaceAll("[^0-9]+", "");

            voiceChannel = guild.getVoiceChannelById(channelRegex);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { voiceChannel = null; }

        if (voiceChannel == null) {
            Extra.sendExpireMessage(channel, "Canal não encontrado, por favor, especifique um canal de voz válido.", 10000);
            message.delete().queue();
            return;
        }

        if (voiceChannel.getMembers().size() == 0) {
            Extra.sendExpireMessage(channel, "O canal de voz já está vazio.", 5000);
            message.delete().queue();
            return;
        }

        // Always disconnect every user
        disconnect(voiceChannel, null, guild);

        channel.sendMessage("<@" + author.getIdLong() + "> todos os membros de `#" + voiceChannel.getName() + "` foram desconectados.").queue();
        message.delete().queue();
    }

    public static void run(SlashCommandInteractionEvent event) {

        OptionMapping option = event.getOption("filter");

        if (event.getGuild().getIdLong() != 624008072544780309L) {
            event.reply("Command is still in development stages...").queue();
            return;
        }

        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildChannelUnion channelTarget = event.getOption("channel").getAsChannel();
        String filter;
        VoiceChannel voiceChannel = channelTarget.asVoiceChannel();
        List<Member> connected = voiceChannel.getMembers();

        if (option != null) filter = option.getAsString();
        else filter = null;

        if (!event.isFromGuild() || member == null || member.hasPermission(Permission.MANAGE_SERVER)) return;

        if (voiceChannel.getMembers().size() == 0) {
            event.reply("Não tem ninguém conectado à este canal de voz.").setEphemeral(true).queue();
            return;
        }

        if (filter == null) {
            event.reply("Desconectando todos os mebros do canal de voz `#" + voiceChannel.getName() + "`.").queue();

            connected.forEach(m -> {
                if (guild != null) guild.kickVoiceMember(m).queue();
            });
            return;
        }

        try {
            switch (filter) {
                case "staff" -> disconnect(voiceChannel, Roles.ROLE_STAFF, guild);

                case "eventos" -> disconnect(voiceChannel, Roles.ROLES_EVENTOS, guild);
            }
        } catch (IllegalArgumentException e) {
            event.reply("Could not find a role for filter `" + filter + "`, please contact the server administration.").queue();
        }
    }

    private static void disconnect(VoiceChannel voiceChannel, long filter, Guild guild) {
        Role role = guild.getRoleById(filter);

        if (role == null) throw new IllegalArgumentException("Filter role cannot be null");

        voiceChannel.getMembers().forEach(member -> {
            if (!member.getRoles().contains(role)) guild.kickVoiceMember(member).queue();
        });
    }

    private static void disconnect(VoiceChannel voiceChannel, List<Long> filters, Guild guild) {
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