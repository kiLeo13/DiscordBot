package bot.misc.features;

import bot.internal.data.BotData;
import bot.util.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class OnBotPing extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        String content = message.getContentRaw();

        if (member == null || member.getUser().isBot()) return;

        if (content.startsWith("<@" + guild.getSelfMember().getId() + ">"))
            Bot.tempReply(message, "Olá! Meu prefixo é `" + BotData.PREFIX + "`", 30000);
    }
}