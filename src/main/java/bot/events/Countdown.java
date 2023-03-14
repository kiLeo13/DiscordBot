package bot.events;

import bot.util.Channels;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Countdown extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannelUnion channel = event.getChannel();
        String[] args = content.split(" ");
        User author = event.getAuthor();

        if (author.isBot()) return;

        if (!content.toLowerCase(Locale.ROOT).startsWith(".cd")
                && !content.toLowerCase(Locale.ROOT).startsWith(".countdown")) return;

        if (!channel.getId().equals(Channels.REQUIRED_COUNTDOWN_CHANNEL.get())
                && !Channels.REQUIRED_COUNTDOWN_CHANNEL.get().toLowerCase(Locale.ROOT).equalsIgnoreCase("none")) return;

        System.out.println("Chegou aqui");

        if (args.length < 2) return;
        int limit = 0;
        String reason;

        try {
            limit = Integer.parseInt(args[1]);

            StringBuilder builder = new StringBuilder();

            for (int i = 2; i < args.length-1; i++)
                builder.append(args[i]).append(" ");

            reason = builder
                    .toString()
                    .stripTrailing();
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
        catch (NullPointerException | StringIndexOutOfBoundsException e) { reason = "none"; }

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
            event.getChannel().sendMessage("Countdown will start soon, please wait.")
                    .delay(5000, TimeUnit.MILLISECONDS)
                    .flatMap(Message::delete)
                    .queue();

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
                if (!hasReason) botSentMessage.editMessage(count + "s")
                        .queue();

                else botSentMessage.editMessage(countReason + " será em " + count + " segundos.")
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

        message.reply("For spamming purposes you can only enter a number between 3 to 60")
                .delay(10000, TimeUnit.MILLISECONDS)
                .flatMap(Message::delete)
                .queue();

        message.delete()
                .queueAfter(11000, TimeUnit.MILLISECONDS);
    }
}