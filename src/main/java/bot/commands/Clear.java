package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Responses;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Clear extends BotCommand {
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

        clear(channel, amount);
    }

    private int toInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void clear(TextChannel channel, int input) {
        isCleaning = true;
        int amount = Math.min(100, input);

        // If it's 0 so the clear process has successfully ended
        if (amount <= 0) {
            Bot.tempEmbed(
                    channel,
                    Responses.success("🧹 Pronto", "`" + input + "` mensagens foram limpadas.", channel.getGuild().getIconUrl()),
                    10000
            );
            isCleaning = false;
            return;
        }

        channel.getHistory().retrievePast(amount).queue(ms -> {

            if (ms == null || ms.isEmpty()) {
                Bot.tempEmbed(
                        channel,
                        Responses.error("❌ Nada encontrado", "Nenhuma mensagem foi encontrada.", null),
                        10000
                );
                return;
            }

            // I fucking hope it does not hit the rate limit lol
            channel.deleteMessagesByIds(ms.stream().map(Message::getId).toList())
                    .queue(v -> clear(channel, amount - ms.size()));
        }, e -> {
            e.printStackTrace();
            Bot.tempEmbed(
                    channel,
                    Responses.error("❌ Ocorreu um erro", "Causa: `" + e.getMessage() + "`.", null),
                    10000
            );
        });
    }
}