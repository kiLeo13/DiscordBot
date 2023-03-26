package bot.commands;

import bot.data.BotFiles;
import bot.util.Channels;
import bot.util.Extra;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.io.FileNotFoundException;
import java.util.List;

import static bot.util.Extra.*;

public class Puta {
    private Puta() {}

    public static void run(Message message) {

        if (message.getGuild().getIdLong() != 1111L) {
            List<String> blocked = List.of(
                    "O comando foi desativado devido à uso incorreto.",
                    "O comando foi desativado devido à comportamento abusivo.",
                    "Puta sua mãe."
            );

            int random = (int) (Math.random() * blocked.size());

            Extra.sendExpireMessage(message.getChannel(),
                    "<@" + message.getAuthor().getIdLong() + "> " + blocked.get(random),
                    10000);

            message.delete().queue();
            return;
        }

        List<Long> allowedSwearingChannels = Channels.COMMAND_PUTA_CHANNELS;

        Member mentionedMember = null;
        String[] args = message.getContentRaw().split(" ");
        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();

        if (!allowedSwearingChannels.contains(channel.getIdLong())) return;
        if (member == null) return;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE)) return;

        List<String> swearingList;

        try {
            swearingList = BotFiles.getSwearings().get("swearings");

            if (args.length >= 2) mentionedMember = message.getMentions().getMembers().get(0);
        } catch (FileNotFoundException e) {
            sendExpireMessage(channel,
                    "Arquivo `swearings.yml` não foi encontrado.",
                    10000);
            System.out.println("Arquivo 'swearings.yml' não foi encontrado, ignorando comando...");
            return;
        } catch (IndexOutOfBoundsException e) {
            sendExpireReply(message, "Membro `" + args[1] + "` não encontrado.", 5000);
            deleteAfter(message, 6000);
            return;
        }

        int random = (int) Math.floor(Math.random() * swearingList.size());
        String swearSentence = swearingList.get(random);

        if (mentionedMember == null) message.reply(swearSentence).queue();
        else {
            channel.sendMessage(mention(mentionedMember) + " " + swearSentence).queue();
            message.delete().queue();
        }
    }

    private static String mention(Member member) {
        return "<@" + member.getIdLong() + ">";
    }
}