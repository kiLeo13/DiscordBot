package bot.commands;

import bot.data.BotFiles;
import bot.util.Requirements;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.io.FileNotFoundException;
import java.util.List;

import static bot.util.Extra.sendExpireMessage;

public class Puta {
    private Puta() {}

    public static void run(Message message) {

        List<Long> allowedSwearingChannels = Requirements.HANDLER_SWEARING_CHANNELS.get();
        if (allowedSwearingChannels.isEmpty()) return;

        Member mentionedMember = null;
        String[] args = message.getContentRaw().split(" ");
        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();

        List<String> swearingList;

        try {
            swearingList = BotFiles.getSwearings().get("swearings");
        } catch (FileNotFoundException e) {
            sendExpireMessage(channel,
                    "File `swearings.yml` was not found.",
                    10000);
            System.out.println("Arquivo 'swearings.yml' não foi encontrado, ignorando comando...");
            return;
        }

        if (!allowedSwearingChannels.contains(channel.getIdLong())) return;
        if (args.length >= 2) mentionedMember =  message.getMentions().getMembers().get(0);

        if (member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        int random = (int) Math.floor(Math.random() * swearingList.size());

        if (mentionedMember == null) message.reply(swearingList.get(random)).queue();
        else {
            channel.sendMessage("<@" + mentionedMember.getId() + "> " + swearingList.get(random)).queue();
            message.delete().queue();
        }
    }
}