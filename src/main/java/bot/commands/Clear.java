package bot.commands;

import bot.util.Bot;
import bot.util.content.Messages;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@CommandPermission(permissions = Permission.MESSAGE_MANAGE)
public class Clear implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        String[] args = message.getContentRaw().split(" ");

        byte amount;

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        try {
            amount = Byte.parseByte(args[1]);
        } catch (NumberFormatException e) {
            Bot.tempMessage(channel, "Valor `amount:` inválido. Por favor forneça um numero entre `1 e 100`.", 10000);
            return;
        }

        if (amount < 1 || amount > 100) {
            Bot.tempMessage(channel, "A quantidade de mensagens a serem apagadas deve estar entre `1 e 100`.\n*Será possível até __1000__ em breve (ou quando o Leo13 parar de procrastinar e adicionar essa função extra em mim)*", 10000);
            return;
        }

        channel.getHistory().retrievePast(amount + 1).queue(msgs -> {
            int deleted = msgs.size();
            channel.purgeMessages(msgs);
            Bot.tempMessage(channel, String.format(
                    "Prontinho, `%s %s` %s 👍",
                    deleted < 10 ? "0" + deleted : deleted,
                    deleted == 1 ? "mensagem" : "mensagens",
                    deleted == 1 ? "foi apagada" : "foram apagadas"
            ), 5000);
        });
    }
}