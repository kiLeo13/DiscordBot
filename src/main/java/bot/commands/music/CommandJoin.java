package bot.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class CommandJoin {
    private CommandJoin() {}

    protected static void run(Message message) {

        User author = message.getAuthor();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        VoiceChannel vChannel;

        if (author.isBot()) return;

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        try {
            vChannel = member.getVoiceState().getChannel().asVoiceChannel();
        } catch (NullPointerException e) {
            channel.sendMessage("<@" + author.getIdLong() + "> você não está contectado à um canal voz.").queue();
            message.delete().queue();
            return;
        }

        

        message.delete().queue();
    }
}