package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CommandPermission(permission = Permission.MANAGE_SERVER)
public class Say implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        List<Message.Attachment> attachments = message.getAttachments();
        List<FileUpload> fileUploads = new ArrayList<>();
        MessageCreateBuilder builder = new MessageCreateBuilder();
        String[] args = content.split(" ");

        Message replyiedMessage = message.getReferencedMessage();

        List<InputStream> streams = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        attachments.forEach(f -> {
            fileNames.add(f.getFileName());
            try { streams.add(f.getProxy().download().get()); }
            catch (InterruptedException | ExecutionException ignore) {}
        });

        int num = 0;
        if (!streams.isEmpty()) {
            for (InputStream is : streams) {
                fileUploads.add(FileUpload.fromData(is, fileNames.get(num++)));
            }
        }

        builder.addFiles(fileUploads);
        if (args.length >= 2) builder.setContent(content.substring(args[0].length() + 1));

        // If the message to be sent is empty, tell them about it
        if (builder.isEmpty()) {
            Bot.sendGhostMessage(channel, "A mensagem a ser enviada não pode estar vazia.", 10000);
            return;
        }

        if (replyiedMessage == null) channel.sendMessage(builder.build()).queue();
        else replyiedMessage.reply(builder.build()).queue();
    }
}