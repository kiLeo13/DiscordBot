package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class Clear implements CommandExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String[] args = message.getContentRaw().split(" ");
        byte amount;

        if (member == null || !member.hasPermission(Permission.MESSAGE_MANAGE)) return;

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
            Bot.deleteAfter(message, 10500);
            return;
        }

        List<Message> history = channel.getHistory().retrievePast(amount + 1).complete();

        String amountStr = amount < 10 ? "0" + amount : String.valueOf(amount);
        String suffix = amount == 1 ? "mensagem" : "mensagens";
        String suffixGone = amount == 1 ? "foi" : "foram";

        channel.purgeMessages(history);
        Bot.sendGhostMessage(channel, String.format("Prontinho, `%s %s` se %s 👍", amountStr, suffix, suffixGone), 5000);
    }
}