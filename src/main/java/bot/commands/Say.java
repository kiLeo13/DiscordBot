package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Say extends BotCommand {

    public Say(String name) {
        super(true, 0, Permission.MANAGE_SERVER, "{cmd} [content]", name);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
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
        if (args.length != 0) builder.setContent(content.substring(4));

        // If the message to be sent is empty, tell them about it
        if (builder.isEmpty()) {
            Bot.tempMessage(channel, "A mensagem a ser enviada não pode estar vazia.", 10000);
            return;
        }

        if (replyiedMessage == null) channel.sendMessage(builder.build()).queue();
        else replyiedMessage.reply(builder.build()).queue();
    }
}