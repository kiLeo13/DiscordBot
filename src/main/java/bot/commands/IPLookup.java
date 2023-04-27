package bot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import bot.util.CommandExecutor;
import bot.util.Messages;
import bot.util.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class IPLookup implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);

        if (member == null) return;

        if (args.length < 2) {
            Bot.sendGhostMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            message.delete().queue();
            return;
        }

        Matcher matcher = pattern.matcher(args[1]);

        if (!matcher.matches()) {
            Bot.sendGhostMessage(channel, "O IP `" + args[1] + "` é inválido.", 10000);
            message.delete().queue();
            return;
        }

        String returned = Bot.request("http://ip-api.com/json/" + args[1]);
        IP ip = deserialize(returned);

        if (ip == null) {
            Bot.sendGhostMessage(channel,
                "Não foi possível encontrar a região do ip `" + args[1] + "`.",
                10000);
            message.delete().queue();
            return;
        }

        channel.sendMessage(String.format("""
            **[📡]** <@%d> Aqui está!

            IP: `%s`
            Country: `%s`
            Country Code: `%s`
            Region: `%s`
            City: `%s`
            Timezone: `%s`
            ISP: `%s`

            *Regiões são aproximadas e podem gerar resultados imprecisos.*
            """,
                member.getIdLong(),
                ip.query,
                ip.country,
                ip.countryCode,
                ip.regionName,
                ip.city,
                ip.timezone,
                ip.isp)).queue();
        
        message.delete().queue();
    }

    private IP deserialize(String ip) {
        Gson gson = new Gson();

        IP value = gson.fromJson(ip, IP.class);

        if (!value.status.equals("success"))
            return null;

        return value;
    }

    private static final class IP {
        private String query;
        private String status;
        private String country;
        private String countryCode;
        private String regionName;
        private String city;
        private String timezone;
        private String isp;
    }
}