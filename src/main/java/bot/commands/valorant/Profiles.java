package bot.commands.valorant;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import bot.internal.managers.requests.RequestManager;

import bot.util.content.Responses;
import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

public class Profiles extends BotCommand {
    private static final Gson gson = new Gson();
    private static final RequestManager requester = RequestManager.create();
    private long lastUsed;

    public Profiles(String name) {
        super(true, 1, null, "{cmd} <user#tag>", name);
    }

    @Override
    public void run(Message message, String[] args) {
        
        Member member = message.getMember();
        TextChannel channel = message.getChannel().asTextChannel();
        Guild guild = message.getGuild();
        long now = System.currentTimeMillis();

        if (now - lastUsed < 2000) {
            Bot.tempMessage(channel, "Por favor, aguarde `02s` entre usos para este comando.", 10000);
            return;
        }

        String name = name(args);
        String tag = tag(args);
        Player player = fetchPlayer(name, tag);

        // Updating cooldown
        lastUsed = System.currentTimeMillis();

        if (player == null) {
            Bot.tempEmbed(
                    channel,
                    Responses.error(null, "Usuário `" + name + "#" + tag + "` não encontrado.", null),
                    10000
            );
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        send.setContent(member.getAsMention());
        send.setEmbeds(embed(player, guild));

        channel.sendMessage(send.build()).queue();
    }
    
    private String name(String[] args) {
        return resolve(args)[0];
    }

    private String tag(String[] args) {
        return resolve(args)[1];
    }

    private String[] resolve(String[] args) {
        final StringBuilder builder = new StringBuilder();

        for (String s : args)
            builder.append(s).append(" ");

        String str = builder.toString().stripTrailing();

        return str.split("#");
    }

    private MessageEmbed embed(Player player, Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();

        String usertag = String.format("%s#%s", player.name, player.tag);
        String level = player.account_level < 10
                ? "0" + player.account_level
                : String.valueOf(player.account_level);

        builder
                .setTitle(player.name)
                .setDescription("Informações de `" + usertag + "`")
                .setThumbnail(player.card.small)
                .setColor(new Color(255, 70, 84))
                .addField("🌎 Região", "`" + player.region.toUpperCase() + "`", true)
                .addField("💻 UUID", "`" + player.puuid + "`", true)
                .addField("👑 Nível", "`" + level + "`", true)
                .setImage(player.card.wide)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private Player fetchPlayer(String name, String tag) {
        String resposne = requester.requestAsString("https://api.henrikdev.xyz/valorant/v1/account/" + name + "/" + tag, null);
        ReceivedData input = gson.fromJson(resposne, ReceivedData.class);

        if (input == null || input.status != 200)
            return null;
        else
            return input.data;
    }

    private record ReceivedData(int status, Player data) {}

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