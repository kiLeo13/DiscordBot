package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Messages;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

@CommandPermission(permissions = Permission.MESSAGE_MANAGE)
public class Clear extends BotCommand {

    public Clear(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();

        byte amount;

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        try {
            amount = Byte.parseByte(args[0]);
        } catch (NumberFormatException e) {
            Bot.tempMessage(channel, "Valor `amount` inválido. Por favor forneça um numero entre `1 e 100`.", 10000);
            return;
        }

        if (amount < 1 || amount > 100) {
            Bot.tempMessage(channel, "A quantidade de mensagens a serem apagadas deve estar entre `1 e 100`.\n*Será possível até __1000__ em breve (ou quando o Leo13 parar de procrastinar e adicionar essa função extra em mim)*", 10000);
            return;
        }

        channel.getHistory().retrievePast(amount + 1).queue(msgs -> {
            if (msgs.isEmpty()) {
                Bot.tempMessage(channel, "Nenhuma mensagem foi encontrada este canal de texto.", 10000);
                return;
            }

            int deleted = msgs.size() - 1;
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