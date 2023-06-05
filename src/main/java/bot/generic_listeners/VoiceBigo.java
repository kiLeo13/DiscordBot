package bot.generic_listeners;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Voices;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoiceBigo extends ListenerAdapter {

    @SubscribeEvent
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        AudioChannel channelJoined = event.getChannelJoined();
        AudioChannel channel = channelJoined == null ? event.getChannelLeft() : channelJoined;
        Guild guild = event.getGuild();
        MessageChannel salada = guild.getTextChannelById(Channels.SALADA.id());
        VoiceChannel saladaVC = guild.getVoiceChannelById(Voices.SALADA.id());

        if (salada == null || saladaVC == null || channel == null) return;

        if (!channel.getId().equals(saladaVC.getId()))
            return;

        final List<Member> members = channel.getMembers();

        if (members.size() == 5) {
            Bot.tempMessage(salada, "<@974159685764649010> a call `" + saladaVC.getName() + "` está com 5 membros, não entre.", 300000);
            return;
        }

        if (members.size() == 4)
            Bot.tempMessage(salada, "<@974159685764649010> a call `" + saladaVC.getName() + "` está com menos de 5 membros, você já pode entrar.", 300000);
    }
}