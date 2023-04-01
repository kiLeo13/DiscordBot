package bot.commands;

import bot.util.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Say implements Command {

    @Override
    public void help(Message message) {

    }

    @Override
    public void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        List<Message.Attachment> attachments = message.getAttachments();
        List<FileUpload> fileUploads = new ArrayList<>();
        MessageCreateBuilder builder = new MessageCreateBuilder();
        String[] args = content.split(" ");

        Message replyiedMessage = message.getReferencedMessage();

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;
        if (author.isBot()) return;

        List<InputStream> streams = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        attachments.forEach(f -> {
            fileNames.add(f.getFileName());
            try { streams.add(f.getProxy().download().get()); }
            catch (InterruptedException | ExecutionException ignore) {}
        });

        AtomicInteger num = new AtomicInteger(0);
        if (!streams.isEmpty()) streams.forEach(f -> fileUploads.add(FileUpload.fromData(f, fileNames.get(num.getAndIncrement()))));

        builder.addFiles(fileUploads);
        if (args.length >= 2) builder.setContent(content.substring(5));

        if (replyiedMessage == null) channel.sendMessage(builder.build()).queue();
        else replyiedMessage.reply(builder.build()).queue();
        message.delete().queue();
    }
}