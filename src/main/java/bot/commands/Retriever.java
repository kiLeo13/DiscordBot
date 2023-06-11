package bot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bot.util.Bot;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.NotNull;

@CommandPermission(permissions = Permission.ADMINISTRATOR)
public class Retriever implements CommandExecutor {
    private static final File file = new File("C:/Users/Leonardo/Desktop/ServerMembers.txt");

    @Override
    public void run(@NotNull Message message) {
        
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        
        channel.sendMessage("Retrieving members...").queue(m -> {
            guild.findMembersWithRoles().onSuccess(list -> {
                updateFile(list);

                final MessageEditBuilder edit = new MessageEditBuilder();

                edit.setFiles(FileUpload.fromData(file, file.getName()))
                    .setContent("Aqui estão! `" + guild.getMemberCount() + "` membros foram registrados.");

                m.editMessage(edit.build()).queue();
            }).onError(e -> {
                m.editMessage("Could not retrieve members.").queue();
            });
        });
    }

    private void updateFile(List<Member> members) {
        final List<String> tags = new ArrayList<>();
        tags.addAll(members.stream().map(m -> m.getUser().getName()).toList());
        Collections.sort(tags);

        StringBuilder builder = new StringBuilder();

        for (String tag : tags)
            builder.append(tag + "\n");

        String result = builder.toString().stripTrailing();
        Bot.write(result, file);
    }
}