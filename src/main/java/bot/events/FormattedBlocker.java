package bot.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class FormattedBlocker extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        
        Message message = event.getMessage();
        String content = message.getContentRaw();
        Member member = message.getMember();

        if (member == null || member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        if (content.startsWith("# ") || 
            content.startsWith("## ") ||
            content.startsWith("### ")) {
            message.delete().queue();
        }
    }
}