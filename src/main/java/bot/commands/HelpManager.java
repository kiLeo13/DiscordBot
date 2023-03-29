package bot.commands;

import bot.util.StaffRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.awt.*;

public class HelpManager {
    private HelpManager() {}

    public static void run(Message message) {

        User author = message.getAuthor();
        String content = message.getContentRaw();
        Member member = message.getMember();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        if (author.isBot()) return;
        if (member == null || (!member.hasPermission(Permission.MESSAGE_MANAGE) && member.getRoles().contains(guild.getRoleById(StaffRoles.ROLE_STAFF.toId())))) return;

        if (args.length < 2 || args[1].startsWith(".help help")) {
            channel.sendMessageEmbeds(generalHelp(guild)).queue();
            message.delete().queue();
            return;
        }

        switch (args[1]) {
            case "bigo" -> BigoAnnouncement.help(message);

            case "disconnect" -> Disconnect.help(message);

            case "commands", "cmds" -> channel.sendMessageEmbeds(helpCommandsEmbed(guild)).queue();

            case "voicemoveall", "moveall", "voiceall" -> VoiceMoveAll.help(message);

            default -> channel.sendMessage("Comando não encontrado, por favor digite o nome de um comando válido. Para saber todos os comandos válidos use `.help`").queue();
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