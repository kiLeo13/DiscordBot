package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Clear extends BotCommand {
    private static long lastUse;

    public Clear(String name) {
        super(true, 1, Permission.MESSAGE_MANAGE, "{cmd} <amount>", name);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        long now = System.currentTimeMillis();
        int amount = toInt(args[0]);

        // Just to be sure we will never hit the rate-limit
        if ((now - lastUse) < 1000) {
            Bot.tempMessage(channel, "Aguarde pelo menos `1s` entre usos para este comando.", 5000);
            return;
        }

        if (amount <= 0 || amount > 99) {
            Bot.tempEmbed(channel, Responses.ERROR_INVALID_ARGUMENTS, 10000);
            return;
        }

        channel.getHistory().retrievePast(amount + 1).queue(ms -> {

            if (ms == null || ms.isEmpty()) {
                Bot.tempMessage(channel, "Nenhuma mensagem foi encontrada.",5000);
                return;
            }

            if (ms.size() == 1) {
                Bot.delete(ms.get(0));
                success(channel, 1);
                return;
            }

            channel.deleteMessagesByIds(ms.stream().map(Message::getId).toList()).queue(v -> {
                success(channel, ms.size());
                lastUse = System.currentTimeMillis();
            });
        });
    }

    private int toInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void success(TextChannel channel, int amount) {
        Bot.tempMessage(channel, String.format("Pronto! `%s` %s!",
                amount < 10 ? "0" + amount : amount,
                amount == 1 ? "mensagem foi apagada" : "mensagens foram apagadas"
                ),
                5000
        );
    }
}