package bot.misc.features;

import bot.internal.data.BotData;
import bot.util.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockDumbCommands extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        Message message = event.getMessage();
        User author = message.getAuthor();
        MessageChannelUnion channel = message.getChannel();
        List<MessageEmbed> embeds = message.getEmbeds();

        if (author.getIdLong() != 297153970613387264L || embeds.isEmpty()) return;

        for (MessageEmbed embed : embeds) {
            if (shouldDelete(embed)) {
                Bot.delete(message);
                channel.sendMessage("Este comando é recomendável que use o bot da Oficina.\nEx: Use `" + BotData.PREFIX + "userinfo` ou `" + BotData.PREFIX + "avatar` ao invés disso.").queue();
            }
        }
    }

    private boolean shouldDelete(MessageEmbed embed) {
        MessageEmbed.AuthorInfo author = embed.getAuthor();
        String authorName = author == null ? null : author.getName();

        List<MessageEmbed.Field> fields = embed.getFields();

        if (fields.isEmpty() && embed.getImage() != null)
            return true;

        for (MessageEmbed.Field field : fields) {
            String name = field.getName();

            if (name != null && name.startsWith("Exibe"))
                return true;

            if (authorName != null && authorName.startsWith("Informações"))
                return true;

            if (name != null && name.stripTrailing().toUpperCase().endsWith("ID"))
                return true;
        }

        return false;
    }
}