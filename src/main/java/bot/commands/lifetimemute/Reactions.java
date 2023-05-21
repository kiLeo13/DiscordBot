package bot.commands.lifetimemute;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Reactions extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {

        Member member = event.getMember();

        if (LifeMuteCommand.isLifeMuted(member))
            event.getReaction().removeReaction(member.getUser()).queue();
    }
}