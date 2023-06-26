package bot.commands;

import java.awt.*;
import java.util.regex.Pattern;

import bot.internal.abstractions.BotCommand;
import bot.util.content.Responses;
import com.google.gson.Gson;

import bot.internal.managers.requests.RequestManager;
import bot.util.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class IPLookup extends BotCommand {
    private static final RequestManager requester = RequestManager.create();

    public IPLookup(String name) {
        super(true, 1, null, "{cmd} <ip>", name);
    }

    @Override
    public void run(Message message, String[] args) {
        
        Member member = message.getMember();
        TextChannel channel = message.getChannel().asTextChannel();
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);
        Guild guild = message.getGuild();

        if (!pattern.matcher(args[0]).matches()) {
            Bot.tempEmbed(
                    channel,
                    Responses.error("❌ O IP `" + args[0] + "` é inválido.", null, null),
                    10000
            );
            return;
        }

        IP ip = getIpData(args[0]);

        if (ip == null) {
            Bot.tempEmbed(
                    channel,
                    Responses.warn("❌ Não foi possível encontrar a região do ip `" + args[0] + "`.", null, null),
                    10000
            );
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();
        MessageEmbed embed = embed(guild, ip);

        send.setContent(member.getAsMention() + " *Esta aproximação pode gerar resultados imprecisos.*");
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

    private IP getIpData(String ip) {
        String response = requester.requestAsString("http://ip-api.com/json/" + ip, null);
        Gson gson = new Gson();
        IP value = gson.fromJson(response, IP.class);

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