package bot.commands.misc;

import bot.util.StaffRoles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordFilter extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message message = event.getMessage();
        String input = message.getContentRaw();
        String[] args = input.split("");
        String regex = "m\\w*ss?\\w*c\\w*r\\w*e\\w*";
        StringBuilder builder = new StringBuilder();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        Role role = guild.getRoleById(StaffRoles.ROLE_STAFF_OFICINA.id());

        if (member == null) return;

        // If role is null, nobody is allowed to do so
        if (role != null && member.getRoles().contains(role)) return;

        for (String i : args) {
            switch (i) {

                case "0" -> builder.append("o");
                case "1" -> builder.append("i");
                case "3" -> builder.append("e");
                case "4" -> builder.append("a");
                case "5" -> builder.append("s");
                case "6" -> builder.append("g");
                case "8" -> builder.append("b");

                default -> builder.append(i.toLowerCase());
            }
        }

        String content = builder.toString();
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find())
            message.delete().queue();
    }
}