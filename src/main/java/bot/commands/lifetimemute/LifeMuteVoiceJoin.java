package bot.commands.lifetimemute;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class LifeMuteVoiceJoin extends ListenerAdapter {

    @SubscribeEvent
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        AudioChannelUnion channelJoined = event.getChannelJoined();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (channelJoined == null || member.getUser().isBot()) return;

        if (LifeMuteCommand.isLifeMuted(member))
            guild.kickVoiceMember(member).queue();
    }
}