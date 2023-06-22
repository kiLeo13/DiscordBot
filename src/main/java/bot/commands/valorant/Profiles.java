package bot.commands.valorant;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.util.content.Messages;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.internal.managers.requests.RequestManager;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@CommandPermission()
public class Profiles extends BotCommand {
    private static final Gson gson = new Gson();
    private static final RequestManager requester = RequestManager.create();
    private long lastUsed;

    public Profiles(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        long now = System.currentTimeMillis();

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        if (now - lastUsed < 2000) {
            Bot.tempMessage(channel, "Por favor, aguarde `02s` entre usos para este comando.", 10000);
            return;
        }

        Player player = fetchPlayer(args);

        // Updating cooldown
        lastUsed = System.currentTimeMillis();

        if (player == null) {
            Bot.tempMessage(channel, "Usuário `" + resolve(args, true) + "#" + resolve(args, false) + "` não foi encontrado.", 10000);
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        send.setContent(member.getAsMention());
        send.setEmbeds(embed(player, guild));

        channel.sendMessage(send.build()).queue();
    }
    
    // This will return the exact input tag
    private String resolve(String[] args, boolean isName) {
        final StringBuilder builder = new StringBuilder();

        for (String s : args)
            builder.append(s).append(" ");

        String str = builder.toString().stripTrailing();

        return str.split("#")[isName ? 0 : 1];
    }

    private MessageEmbed embed(Player player, Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle(player.name)
                .setDescription("Informações de `" + player.name + "#" + player.tag + "`")
                .setThumbnail(player.card.small)
                .setColor(new Color(255, 70, 84))
                .addField("🌎 Região", "`" + player.region.toUpperCase() + "`", true)
                .addField("💻 UUID", "`" + player.puuid + "`", true)
                .addField("👑 Nível", player.account_level < 10
                    ? "`0" + player.account_level + "`"
                    : "`" + player.account_level + "`", true)
                .setImage(player.card.wide)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private Player fetchPlayer(String[] args) {
        String resposne = requester.requestAsString("https://api.henrikdev.xyz/valorant/v1/account/" + resolve(args, true) + "/" + resolve(args, false), null);
        PlayerInput input = gson.fromJson(resposne, PlayerInput.class);

        if (input == null || input.status != 200)
            return null;
        else
            return input.data;
    }

    private record PlayerInput(int status, Player data) {}

    private record Player(
            String name,
            String tag,
            String puuid,
            String region,
            int account_level,
            Card card
    ) {}

    private record Card(
            String small,
            String wide
    ) {}
}