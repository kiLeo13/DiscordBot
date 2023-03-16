package bot.events;

import bot.util.Channels;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static bot.util.Feature.replyBombMessage;
import static bot.util.Feature.sendBombMessage;

public class Countdown extends ListenerAdapter {



    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        List<Long> allowedCountdownChannels = Channels.COUNTDOWN_CHANNELS.get();

        if (allowedCountdownChannels.isEmpty()) return;
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannelUnion channel = event.getChannel();
        String[] args = content.split(" ");
        User author = event.getAuthor();

        if (!content.toLowerCase(Locale.ROOT).startsWith(".countdown")
                && !content.toLowerCase(Locale.ROOT).startsWith(".cd")) return;

        if (!allowedCountdownChannels.contains(channel.getIdLong())) return;

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

            System.out.println("Membro " + author.getName() +
                    "#" + author.getDiscriminator() +
                    " utilizou um formato de numero inválido no .countdown (" + args[1] + ")");

            message.delete().queueAfter(11000, TimeUnit.MILLISECONDS);

            return;
        }
        catch (NullPointerException
               | StringIndexOutOfBoundsException
               | ArrayIndexOutOfBoundsException e) { reason = "none"; }

        if (limit < 3 || limit > 60) {
            callSecondsBoundaries(event);
            return;
        }

        message.delete().queue();
        Timer timer = new Timer(event, limit, reason);
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

        private Timer(MessageReceivedEvent event, int limit, String countReason) {
            sendBombMessage(event.getChannel(),
                    "Countdown will start soon, please wait...",
                    5000);

            try { Thread.sleep(5000); }
            catch (InterruptedException e) { e.printStackTrace(); }

            this.count = limit;
            this.countReason = countReason;
            this.hasReason = !countReason.equalsIgnoreCase("none");
            this.channel = event.getChannel();
            this.author = event.getAuthor();

            this.botSentMessage = channel.sendMessage("Starting countdown...").complete();
        }

        @Override
        public void run() {
            while (count >= 0) {
                if (!hasReason) botSentMessage.editMessage(getFormattedCount(count))
                        .queue();

                else botSentMessage.editMessage(countReason + " será em " + getFormattedCount(count))
                        .queue();

                count--;
                try { Thread.sleep(1000); }
                catch (InterruptedException ignore) {}
            }

            botSentMessage.delete().queue();
            channel.sendMessage("<@" + author.getId() + "> countdown has ended.")
                    .queue();
        }
    }

    private void callSecondsBoundaries(MessageReceivedEvent e) {

        Message message = e.getMessage();

        replyBombMessage(message,
                "For spamming/rate-limit purposes you can only enter a number between 3 to 60",
                10000);

        message.delete()
                .queueAfter(11000, TimeUnit.MILLISECONDS);
    }

    private static String getFormattedCount(int count) {
        return " `" + count + "s`";
    }
}