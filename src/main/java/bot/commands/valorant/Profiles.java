package bot.commands.valorant;

import java.awt.Color;

import bot.util.CommandPermission;
import com.google.gson.Gson;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@CommandPermission()
public class Profiles implements CommandExecutor {
    private long lastUsed;

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        String[] args = content.split(" ");
        long now = System.currentTimeMillis();

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        if (now - lastUsed < 2000) {
            Bot.tempMessage(channel, "Existe um cooldown de `02s` entre usos para este comando, por favor aguarde.", 10000);
            return;
        }

        String request = Bot.request("https://api.henrikdev.xyz/valorant/v1/account/" + name(args) + "/" + tag(args));
        Player player = parse(request);

        // Updating cooldown
        lastUsed = System.currentTimeMillis();

        if (player == null) {
            Bot.tempMessage(channel, "Usuário `" + name(args) + "#" + tag(args) + "` não foi encontrado.", 10000);
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        send.setContent("<@" + member.getIdLong() + ">");
        send.setEmbeds(embed(player, guild));

        channel.sendMessage(send.build()).queue();
    }
    
    // This will return the exact input tag
    private String name(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (String s : args)
            builder.append(s).append(" ");

        return builder.toString().split("#")[0];
    }

    // This will return the exact input name
    private String tag(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (String s : args)
            builder.append(s).append(" ");

        return builder.toString().split("#")[1];
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

    private Player parse(String str) {
        Gson gson = new Gson();

        PlayerInput input = gson.fromJson(str, PlayerInput.class);

        if (input == null || input.status != 200)
            return null;
        else
            return input.data;
    }

    private record PlayerInput(int status, Player data) {}

    private record Player(String name, String tag, String puuid, String region, int account_level, Card card) {}

    private record Card(String small, String wide) {}
}