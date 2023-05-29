package bot.commands;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.util.annotations.CommandPermission;
import bot.util.requests.RequestManager;
import com.google.gson.Gson;

import bot.util.interfaces.CommandExecutor;
import bot.util.Messages;
import bot.util.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@CommandPermission()
public class IPLookup implements CommandExecutor {
    private static final RequestManager requester = RequestManager.NewManager();

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        Matcher matcher = pattern.matcher(args[1]);

        if (!matcher.matches()) {
            Bot.tempMessage(channel, "O IP `" + args[1] + "` é inválido.", 10000);
            return;
        }

        String returned = requester.requestAsString("http://ip-api.com/json/" + args[1], null);
        IP ip = deserialize(returned);

        if (ip == null) {
            Bot.tempMessage(channel,
                "Não foi possível encontrar a região do ip `" + args[1] + "`.",
                10000);
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        MessageEmbed embed = embed(message.getGuild(), ip);

        send.setContent("<@" + member.getIdLong() + "> *Isto é uma aproximação que pode gerar resultados imprecisos.*");
        send.setEmbeds(embed);

        channel.sendMessage(send.build()).queue();
    }

    private MessageEmbed embed(Guild guild, IP ip) {
        EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("🗺 IP Lookup")
                .setDescription("Mostrando informações do IP: `" + ip.query + "`.")
                .setColor(new Color(114, 222, 64))
                .addField("🌎 País", ip.country + " (" + ip.countryCode + ")", true)
                .addField("📌 Região", ip.regionName, true)
                .addField("🏙 Cidade", ip.city, true)
                .addField("🕒 Timezone", ip.timezone, true)
                .addField("📡 Provedor", ip.isp, true)
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }

    private IP deserialize(String ip) {
        Gson gson = new Gson();
        IP value = gson.fromJson(ip, IP.class);

        if (!value.status.equals("success"))
            return null;

        return value;
    }

    private record IP(
            String query,
            String status,
            String country,
            String countryCode,
            String regionName,
            String city,
            String timezone,
            String isp
    ) {}
}