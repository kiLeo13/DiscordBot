package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class Say extends BotCommand {

    public Say(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {

        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        List<Message.Attachment> attachments = message.getAttachments();
        List<FileUpload> fileUploads = new ArrayList<>();
        MessageCreateBuilder builder = new MessageCreateBuilder();

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
            Bot.tempMessage(channel, "A mensagem a ser enviada não pode estar vazia.", 10000);
            return;
        }

        if (replyiedMessage == null) channel.sendMessage(builder.build()).queue();
        else replyiedMessage.reply(builder.build()).queue();
    }
}