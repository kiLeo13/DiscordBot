package bot.listeners;

import bot.util.Bot;
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
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Links extends ListenerAdapter {
    
    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

       Message message = event.getMessage();
       String content = message.getContentRaw();
       Member member = message.getMember();
       User author = message.getAuthor();
       MessageChannelUnion channel = message.getChannel();

       // We should not worry if it's a bot, right?
       if (author.isBot()) return;
       if (member == null || member.hasPermission(Permission.MESSAGE_MANAGE)) return;

       String regex = "https?://\\S+";
       Pattern pattern = Pattern.compile(regex);
       Matcher matcher = pattern.matcher(content);

       if (!matcher.find()) return;

       

       Bot.delete(message);

       Bot.delete(message);
       author.openPrivateChannel().queue(c -> {
          c.sendMessage("Por favor, não envie links no `" + channel.getName() + "`.").queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
       });
    }
}