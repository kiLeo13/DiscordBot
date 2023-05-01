package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

public class Say implements CommandExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        List<Message.Attachment> attachments = message.getAttachments();
        List<FileUpload> fileUploads = new ArrayList<>();
        MessageCreateBuilder builder = new MessageCreateBuilder();
        String[] args = content.split(" ");

        Message replyiedMessage = message.getReferencedMessage();

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        List<InputStream> streams = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        // This is the sentence that is going to be sent (the words provided by the author of the command)
        String sentence = args.length < 2
                ? ""
                : content.substring(args[0].length() + 1);

        if (sentence.equals("{}")) {
            channel.sendMessage("").queue();
            message.delete().queue();
            return;
        }

        attachments.forEach(f -> {
            fileNames.add(f.getFileName());
            try { streams.add(f.getProxy().download().get()); }
            catch (InterruptedException | ExecutionException ignore) {}
        });

        if (sentence.startsWith("{")) {
            if (!streams.isEmpty()) {
                Bot.sendGhostMessage(channel, "Formato JSON para possível embed está sendo ignorado pois arquivos foram anexados.", 5000);
            } else {
                MessageEmbed embed = toJson(sentence);

                if (embed == null) {
                    Bot.sendGhostMessage(channel, "Não foi possível executar formatação para embed.\nInput:\n```json" + sentence + "```", 10000);
                    message.delete().queue();
                } else {
                    builder.setEmbeds(embed);
                    channel.sendMessage(builder.build()).queue();
                }
                return;
            }
        }

        int num = 0;
        if (!streams.isEmpty()) {
            for (InputStream is : streams) {
                fileUploads.add(FileUpload.fromData(is, fileNames.get(num++)));
            }
        }

        builder.addFiles(fileUploads);
        builder.setContent(content.substring(5));

        if (replyiedMessage == null) channel.sendMessage(builder.build()).queue();
        else replyiedMessage.reply(builder.build()).queue();
        message.delete().queue();
    }

    private MessageEmbed toJson(String content) {
        Gson gson = new Gson();
        EmbedBuilder builder = new EmbedBuilder();

        return builder.build();
    }
}