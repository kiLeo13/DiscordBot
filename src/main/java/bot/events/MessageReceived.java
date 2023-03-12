package bot.events;

import bot.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MessageReceived extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();

        // Register command
        if (message.toLowerCase(Locale.ROOT).startsWith("r!")) registerCommand(event);

        // Check if message has prefix (r!)
        if (message.equalsIgnoreCase("r.ping")) pingComand(event);

    }

    private void registerCommand(MessageReceivedEvent e) {

        User author = e.getAuthor();
        Role role = author.getJDA().getRoleById("1009140499325648991");
        Member member = e.getMember();
        MessageChannelUnion channel = e.getChannel();

        if (role == null) {
            channel.sendMessage("Required role was not found.").queue();
            return;
        }
        
        if (author.isBot()) return;
        if (member == null) return;

        if (member.getRoles().contains(role)) {
            channel.sendMessage("É, <@" + member.getId() + "> me parece que vc tem o cargo <@&" + role.getId() + "> :medo:").queue();
        }
    }

    private void pingComand(MessageReceivedEvent e) {

        User author = e.getAuthor();
        MessageChannelUnion channel = e.getChannel();
        JDA api = Main.getApi();

        if (author.isBot()) return;

        channel.sendMessage("Oioioioioioi\nGateway ping: `" + api.getGatewayPing() + "ms`").queue();
    }
}