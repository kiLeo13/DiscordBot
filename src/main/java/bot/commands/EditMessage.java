package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.util.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CommandPermission(permissions = Permission.MANAGE_SERVER)
public class EditMessage extends BotCommand {

    public EditMessage(String name) {
        super(false, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {
        
        TextChannel channel = message.getChannel().asTextChannel();
        Message referenced = message.getReferencedMessage();
        Guild guild = message.getGuild();
        String content = message.getContentRaw();

        if (referenced == null) {
            Bot.tempMessage(channel, "Você não respondeu à nenhuma mensagem para eu editar.", 10000);
            return;
        }

        if (!referenced.getAuthor().getId().equals(guild.getSelfMember().getId())) {
            Bot.tempMessage(channel, "A mensagem selecionada não foi enviada por mim.", 0);
            return;
        }

        List<Attachment> attachments = message.getAttachments();
        List<FileUpload> files = new ArrayList<>(10);
        MessageEditBuilder edit = new MessageEditBuilder();

        for (int i = 0; i < 10; i++) {
            if (attachments.isEmpty() || i >= attachments.size()) break;

            Attachment file = attachments.get(i);
            String fileName = file.getFileName();

            try {
                files.add(FileUpload.fromData(file.getProxy().download().get(), fileName));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (args.length >= 2)
            edit.setContent(content.substring(args[0].length() + 1));

        edit.setFiles(files);
        edit.setEmbeds(referenced.getEmbeds());

        referenced.editMessage(edit.build()).queue(null, e -> {
            Bot.tempMessage(channel, "Não foi possível editar a mensagem.", 10000);
        });
    }
}