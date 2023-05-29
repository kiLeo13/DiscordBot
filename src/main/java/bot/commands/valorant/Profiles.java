package bot.commands.valorant;

import bot.util.Bot;
import bot.util.interfaces.CommandExecutor;
import bot.util.annotations.CommandPermission;
import bot.util.Messages;
import bot.util.requests.RequestManager;
import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

@CommandPermission()
public class Profiles implements CommandExecutor {
    private static final RequestManager requester = RequestManager.NewManager();
    private long lastUsed;

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        String[] args = content.split(" ");
        String[] inputArgs = content.substring(args[0].length() + 1).split(" ");
        long now = System.currentTimeMillis();

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        if (now - lastUsed < 2000) {
            Bot.tempMessage(channel, "Por favor, aguarde `02s` entre usos para este comando.", 10000);
            return;
        }

        String request = requester.requestAsString("https://api.henrikdev.xyz/valorant/v1/account/" + nameTag(inputArgs, true) + "/" + nameTag(inputArgs, false), null);
        Player player = parse(request);

        // Updating cooldown
        lastUsed = System.currentTimeMillis();

        if (player == null) {
            Bot.tempMessage(channel, "Usuário `" + nameTag(inputArgs, true) + "#" + nameTag(inputArgs, false) + "` não foi encontrado.", 10000);
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        send.setContent("<@" + member.getId() + ">");
        send.setEmbeds(embed(player, guild));

        channel.sendMessage(send.build()).queue();
    }
    
    // This will return the exact input tag
    private String nameTag(String[] args, boolean isName) {
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