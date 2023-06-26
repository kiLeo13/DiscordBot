package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clear extends BotCommand {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private boolean isCleaning = false;

    public Clear(String name) {
        super(true, 1, Permission.MESSAGE_MANAGE, "{cmd} <amount>", name);
    }

    @Override
    public void run(Message message, String[] args) {

        TextChannel channel = message.getChannel().asTextChannel();
        int amount = toInt(args[0]);

        if (amount <= 0 || amount > 1000) {
            Bot.tempEmbed(channel, Responses.ERROR_INVALID_ARGUMENTS, 10000);
            return;
        }

        if (isCleaning) {
            Bot.tempEmbed(
                    channel,
                    Responses.warn("⏱ Aguarde", "O bot está limpando mensagens.", null),
                    10000
            );
            return;
        }

        clear(channel, amount, amount);
    }

    private int toInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void clear(TextChannel channel, int input, final int raw) {
        isCleaning = true;
        int amount = Math.min(100, input);

        // If it's 0 so the clear process has successfully ended
        if (amount <= 0) {
            success(channel, raw);
            return;
        }

        channel.getHistory().retrievePast(amount).queue(messages -> {

            if (messages == null || messages.isEmpty()) {
                Bot.tempEmbed(
                        channel,
                        Responses.error("❌ Nada encontrado", "Nenhuma mensagem foi encontrada.", null),
                        10000
                );
                return;
            }

            if (messages.size() == 1) {
                Bot.delete(messages.get(0));
                success(channel, 1);
                return;
            }

            // I fucking hope it does not hit the rate limit
            channel.deleteMessagesByIds(messages.stream().map(Message::getId).toList()).queue(v -> {

                System.out.println("Call cleared " + messages.size() + " messages.");

                // We have to wait at least a second before calling it again
                scheduler.schedule(() -> clear(channel, amount - messages.size(), raw), 1500, TimeUnit.MILLISECONDS);
            });
        });
    }

    private void success(TextChannel channel, int amount) {
        Bot.tempMessage(
                channel,
                String.format("Pronto, `%s` %s!",
                        amount < 10 ? "0" + amount : amount,
                        amount == 1 ? "mensagem foi limpada" : "mensagens foram limpadas"
                ),
                5000
        );
        isCleaning = false;
    }
}