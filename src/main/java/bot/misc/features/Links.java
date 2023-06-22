package bot.misc.features;

import bot.util.Bot;
import bot.util.YamlUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links extends ListenerAdapter {
    
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        String content = message.getContentRaw();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();

        // Ignore it if the member has Manage Messages or if the message was sent in an allowed channel
        if (memberIsAllowed(message)) return;

        String regex = "https?://\\S+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) return;

        Bot.delete(message);
        author.openPrivateChannel()
                .queue(c -> c.sendMessage("Por favor, não envie links no `" + channel.getName() + "`.").queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER)));
    }

    private boolean memberIsAllowed(Message message) {
        
        Member member = message.getMember();
        final List<String> allowed = YamlUtil.readAllowedLinks();
        final List<String> blocked = YamlUtil.readBlockedLinks();

        // We can just ignore it if they do not exist or are a bot
        if (member == null || member.getUser().isBot()) return true;

        String id = message.getChannel().getId();

        return (member.hasPermission(Permission.MESSAGE_MANAGE) || allowed.contains(id)) && !blocked.contains(id);
    }
}