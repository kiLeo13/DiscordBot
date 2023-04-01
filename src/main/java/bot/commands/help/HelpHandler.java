package bot.commands.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public class HelpHandler extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String[] commands = {"commands", "disconnectall", "registrationtake", "say", "voicemoveall"};
        User author = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        String content = event.getMessage().getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = event.getGuild();

        if (member == null || author.isBot()) return;

        if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
            channel.sendMessageEmbeds(generalHelp(guild)).queue();
            message.delete().queue();
            return;
        }

        if (!Arrays.asList(commands).contains(args[1])) {
            channel.sendMessage("Não achamos nenhuma ajuda para ´" + args[1] + "`.").queue();
            message.delete().queue();
            return;
        }



        message.delete().queue();
    }

    private static MessageEmbed generalHelp(Guild guild) {
        EmbedBuilder builder = new EmbedBuilder();

        return builder
                .setColor(Color.YELLOW)
                .setTitle("Ajuda Geral", guild.getIconUrl())
                .setDescription("Esta é a mensagem central de ajuda digitando `.help` você verá como utilizar o `help` e alguns comandos.")
                .addField("> Comandos", "Utilizando `.help <command>` você pode ver como usar um comando específico, sempre terá exemplos de como usaro comando. Para saber todos os comandos use `.help commands`.", true)
                .addField("> Correções", "Se achar que alguma informação fornecida neste comando está não corresponde ao real comando, envie uma mensagem no privado do <@596939790532739075> e será corrigido.", true)
                .setFooter("Oficina Myuu", guild.getIconUrl())
                .build();
    }

    private static MessageEmbed helpCommandsEmbed(Guild guild) {
        EmbedBuilder builder = new EmbedBuilder();

        return builder
                .setColor(Color.PINK)
                .setTitle("Ajuda Comandos", guild.getIconUrl())
                .setDescription("Este comando irá te guiar por todos os comandos do bot. Para a explicação dos comandos, use `.help <command>`, o nome dos comandos estão logo abaixo.")
                .addField("> Commands", """
                        `bigo`
                        `disconnect`
                        `disconnectall`
                        `help`
                        `ping`
                        `roles` (r!)
                        `take`  (r!)
                        `among`
                        `uptime`
                        `moveall`
                        """, true)
                .addField("> Prefix", "O prefixo do bot atualmente é `.`, alguns comandos têm exceção, como o registro (`r!`).", true)
                .build();
    }
}