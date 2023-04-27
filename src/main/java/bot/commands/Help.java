package bot.commands;

import bot.data.BotConfig;
import bot.events.CommandHandler;
import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.RegistrationRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.util.HashMap;

public class Help implements CommandExecutor {

    @Override
    public void run(Message message) {

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        MessageCreateBuilder send = new MessageCreateBuilder();
        Member member = message.getMember();
        Guild guild = message.getGuild();
        Role required = guild.getRoleById(RegistrationRoles.ROLE_REQUIRED.id());

        if (guild.getIdLong() != 1) return;

        if (args.length < 2) {
            channel.sendMessageEmbeds(defaultHelp()).queue();
            message.delete().queue();
        } else {
            /*
             * Translating this crazyness:
             * 'member' cannot be null,
             * 'required' cannot be null,
             * member has to have the role 'required'
             * and the second argument of the message has to match 'r!register' or 'register'
             */
            if ((member != null && required != null && member.getRoles().contains(required)) && (args[1].equalsIgnoreCase("r!register") || args[1].equalsIgnoreCase("register"))) {
                channel.sendMessageEmbeds(Registration.getInstance().help(message)).queue();
                message.delete().queue();
                return;
            }

            MessageEmbed embed = getHelp(message);

            if (embed == null) {
                Bot.sendGhostMessage(channel, "Nenhuma ajuda encontrada para `" + args[1] + "`. Tente inserir o prefixo do bot caso não tenha.", 10000);
                message.delete().queue();
            } else {
                send.addEmbeds(embed);
                send.setContent("<@" + author.getIdLong() + ">");

                channel.sendMessage(send.build()).queue();
                message.delete().queue();
            }
        }
    }

    private MessageEmbed defaultHelp() {
        EmbedBuilder builder = new EmbedBuilder();
        String commands = "";

        builder
                .setColor(Color.CYAN)
                .setTitle("❓ Help Assistance")
                .addField("📝 Comandos", commands, true)
                .addField("", "", true);

        return builder.build();
    }

    private MessageEmbed getHelp(Message message) {
        HashMap<String, CommandExecutor> commands = CommandHandler.getCommands();
        String[] args = message.getContentRaw().split(" ");
        String cmd;
        CommandExecutor command = null;

        if (args[1].startsWith(BotConfig.PREFIX))
            cmd = args[1].substring(1);

        else if (args[1].startsWith(BotConfig.PREFIX_REGISTER))
            cmd = args[1].substring(2);

        else cmd = args[1];

        for (String s : commands.keySet()) {
            String real = s;

            if (s.startsWith(BotConfig.PREFIX)) s = s.substring(1);
            if (s.startsWith(BotConfig.PREFIX_REGISTER)) s = s.substring(2);

            if (s.equalsIgnoreCase(cmd)) command = commands.get(real);
        }

        if (command == null) return null;

        return command.help(message);
    }
}