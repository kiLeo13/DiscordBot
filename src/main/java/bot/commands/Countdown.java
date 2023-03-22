package bot.commands;

import bot.util.Channels;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.util.Extra.*;

public class Countdown {
    private Countdown() {}

    public static void run(Message message) {

        List<Long> allowedCountdownChannels = Channels.COMMAND_COUNTDOWN_CHANNELS;

        String content = message.getContentRaw();
        MessageChannelUnion channel = message.getChannel();
        String[] args = content.split(" ");

        if (allowedCountdownChannels.isEmpty() || !allowedCountdownChannels.contains(channel.getIdLong())) return;
        if (message.getAuthor().isBot()) return;

        if (args.length < 2) return;
        int limit = 0;
        String reason;

        try {
            limit = Integer.parseInt(args[1]);

            if (args[2] == null) throw new NullPointerException("Argument is null");

            StringBuilder builder = new StringBuilder();
            builder.append("`");

            for (int i = 2; i < args.length; i++)
                builder.append(args[i]).append(" ");

            reason = builder
                    .toString()
                    .stripTrailing() + "`";
        } catch (NumberFormatException e) {
            message.reply("The number format provided `" + args[1] + "` is not valid.")
                    .delay(10000, TimeUnit.MILLISECONDS)
                    .flatMap(Message::delete)
                    .queue();

            message.delete().queueAfter(11000, TimeUnit.MILLISECONDS);
            return;
        }
        catch (NullPointerException
               | StringIndexOutOfBoundsException
               | ArrayIndexOutOfBoundsException e) { reason = "none"; }

        if (limit < 3 || limit > 60) {
            callSecondsBoundaries(message);
            return;
        }

        message.delete().queue();
        Timer timer = new Timer(message, limit, reason);
        Thread thread = new Thread(timer);
        thread.start();
    }

    private static class Timer implements Runnable {
        private int count;
        private final String countReason;
        private final boolean hasReason;
        private final Message botSentMessage;
        private final MessageChannelUnion channel;
        private final User author;

        private Timer(Message message, int limit, String countReason) {
            this.count = limit;
            this.countReason = countReason;
            this.hasReason = !countReason.equalsIgnoreCase("none");
            this.channel = message.getChannel();
            this.author = message.getAuthor();

            this.botSentMessage = channel.sendMessage("Starting countdown...").complete();

            try { Thread.sleep(3000); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }

        @Override
        public void run() {
            while (count >= 0) {
                if (!hasReason) botSentMessage.editMessage(getFormattedCount(count))
                        .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                else botSentMessage.editMessage(countReason + " será em " + getFormattedCount(count))
                        .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                count--;
                try { Thread.sleep(1000); }
                catch (InterruptedException ignore) {}
            }

            botSentMessage.delete().queue();
            if (!hasReason) channel.sendMessage("<@" + author.getId() + "> o contador se encerrou.").queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            else channel.sendMessage("<@" + author.getId() + "> o contador para " + countReason + " se encerrou.").queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        }
    }

    private static void callSecondsBoundaries(Message message) {
        sendExpireReply(message,
                "For spamming/rate-limit purposes you can only enter a number between 3 to 60",
                10000);

        deleteAfter(message, 11000);
    }

    private static String getFormattedCount(int count) {
        return "`" + count + "s`";
    }
}