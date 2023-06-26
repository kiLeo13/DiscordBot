package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Disconnect extends BotCommand {

    public Disconnect(String... names) {
        super("{cmd}", names);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        Member member = message.getMember();
        Guild guild = message.getGuild();

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            Bot.tempEmbed(channel, Responses.ERROR_VOICE_CHANNEL_NOT_FOUND, 10000);
            return;
        }

        guild.kickVoiceMember(member).queue(s -> {
            Bot.tempEmbed(channel, Responses.success("Desconectado(a) com sucesso", null, member.getUser().getAvatarUrl()), 10000);
        }, e -> {
            e.printStackTrace();
            Bot.tempEmbed(channel, Responses.error("❌ Não foi possível desconectar", "Causa: `" + e.getMessage() + "`.", null), 10000);
        });
    }
}