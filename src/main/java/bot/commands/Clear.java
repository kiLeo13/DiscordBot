package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.Messages;
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
            Bot.sendGhostMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        try {
            amount = Byte.parseByte(args[1]);
        } catch (NumberFormatException e) {
            Bot.sendGhostMessage(channel, "Valor `amount:` inválido. Por favor forneça um numero entre `1 e 100`.", 10000);
            return;
        }

        if (amount < 1 || amount > 100) {
            Bot.sendGhostMessage(channel, "A quantidade de mensagens a serem apagadas deve estar entre `1 e 100`.\n*Será possível até __1000__ em breve (ou quando o Leo13 parar de procrastinar e adicionar essa função extra em mim)*", 10000);
            return;
        }


        String amountStr = amount < 10 ? "0" + amount : String.valueOf(amount);
        String suffix = amount == 1 ? "mensagem" : "mensagens";
        String suffixGone = amount == 1 ? "foi" : "foram";

        channel.getHistory().retrievePast(amount + 1).queue(channel::purgeMessages);
        Bot.sendGhostMessage(channel, String.format("Prontinho, `%s %s` se %s 👍", amountStr, suffix, suffixGone), 5000);
    }
}