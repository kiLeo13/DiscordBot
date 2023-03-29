package bot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Say {
    private Say() {}

    public static void speak(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        MessageCreateBuilder builder = new MessageCreateBuilder();

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;
        if (author.isBot()) return;

        builder.setContent(content.substring(5));

        channel.sendMessage(builder.build()).queue();
        message.delete().queue();
    }
}