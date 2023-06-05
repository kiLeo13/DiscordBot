package bot.commands;

import bot.util.Bot;
import bot.util.content.Roles;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import bot.util.managers.requests.RequestManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.io.InputStream;

@CommandPermission()
public class Nerd implements CommandExecutor {
    private static final RequestManager manager = RequestManager.create();

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
        Member target = args.length >= 2 ? Bot.fetchMember(guild, args[1]) : member;

        if (salada == null || alfea == null) {
            System.out.println("Could not find role 'salada' or 'alfea'. Ignoring `nerd` command...");
            return;
        }

        if (!(member.getRoles().contains(salada) && member.getRoles().contains(alfea))) return;

        InputStream stream = manager.requestAsStream("https://raw.githubusercontent.com/kiLeo13/DiscordBot/main/content/images/nerd.png", null);
        System.out.println(stream);

        send.setContent("<@" + (target == null ? member.getId() : target.getId()) + ">");

        if (stream == null) {
            Bot.tempMessage(channel, "Não foi possível executar o comando.", 10000);
            message.delete().queue();
            return;
        }

        send.setFiles(FileUpload.fromData(stream, "nerd.png"));
        channel.sendMessage(send.build()).queue();
    }
}