package bot.commands;

import bot.util.Bot;

import java.io.InputStream;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.Roles;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@CommandPermission()
public class Nerd implements CommandExecutor {

    @Override
    public void run(Message message) {

        Member member = message.getMember();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        Role salada = guild.getRoleById(Roles.ROLE_SALADA.id());
        Role alfea = guild.getRoleById(Roles.ROLE_ALFEA.id());
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");
        MessageCreateBuilder send = new MessageCreateBuilder();
        Member target = args.length >= 2 ? Bot.member(guild, args[1]) : member;

        if (salada == null || alfea == null) {
            System.out.println("Could not find role 'salada' or 'alfea'. Ignoring `nerd` command...");
            return;
        }

        if (!(member.getRoles().contains(salada) && member.getRoles().contains(alfea))) return;

        InputStream stream = Bot.requestObject("https://raw.githubusercontent.com/kiLeo13/DiscordBot/main/content/images/nerd.png");

        if (target == null)
            send.setContent("<@" + member.getIdLong() + ">");
        else
            send.setContent("<@" + target.getIdLong() + ">");

        if (stream == null) {
            Bot.tempMessage(channel, "Não foi possível executar o comando.", 10000);
            message.delete().queue();
            return;
        }

        send.setFiles(FileUpload.fromData(stream, "nerd.png"));
        channel.sendMessage(send.build()).queue();
    }
}