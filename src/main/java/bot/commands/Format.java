package bot.commands;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@CommandPermission()
public class Format implements CommandExecutor {

    @Override
    public void run(Message message) {

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();

        if (args.length < 2) {
            channel.sendMessage("Argumentos insuficientes!\nUse: `.format [camel/CAMEL | reverse | time | emoji] <text>`").queue();
            return;
        }

        String formatting = content.substring(args[0].length() + args[1].length() + 2);

        if (formatting.length() > Message.MAX_CONTENT_LENGTH) {
            Bot.sendGhostMessage(channel, "O resultado final terá mais de `" + Message.MAX_CONTENT_LENGTH + "` caracteres por limitação do Discord eu não posso enviar mensagens maiores do que este valor.", 20000);
            return;
        }

        switch (args[1]) {
            case "camel" -> channel.sendMessage(camel(formatting.split(""), false)).queue();

            case "CAMEL" -> channel.sendMessage(camel(formatting.split(""), true)).queue();

            case "reverse" -> channel.sendMessage(Bot.reverse(formatting)).queue();

            case "time" -> channel.sendMessage(time(formatting)).queue();

            case "emoji" -> channel.sendMessage(emoji(formatting.split(""))).queue();
        
            default -> channel.sendMessage("Não achei nenhuma formatação para `" + args[1] + "`.").queue();
        }
    }

    private String camel(String[] array, boolean firstUppercase) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            String letter = array[i];
            if (firstUppercase)
                letter = i % 2 == 0 ? letter.toUpperCase() : letter.toLowerCase();
            else
                letter = i % 2 != 0 ? letter.toUpperCase() : letter.toLowerCase();

            builder.append(letter);
        }

        return builder.toString().stripTrailing();
    }

    private String time(String str) {
        try {
            long epoch = Long.parseLong(str);
            String[] weekDays = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"};
            String[] months = {
                    "Janeiro", "Fevereiro", "Março", "Abril",
                    "Maio", "Junho", "Julho", "Agosto",
                    "Setembro", "Outubro", "Novembro", "Dezembro"
            };

            LocalDateTime value = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);

            String weekDay = weekDays[value.getDayOfWeek().getValue() - 1];
            String month = months[value.getMonth().getValue() - 1];
            int day = value.getDayOfMonth();
            String time = DateTimeFormatter.ofPattern("HH:mm:ss").format(value);
            
            return String.format(
                    "`(UTC)`\n%s, %s de %s de %s às %s.",
                    weekDay,
                    day < 10 ? "0" + day : String.valueOf(day),
                    month,
                    value.getYear(),
                    time
            );
        } catch (NumberFormatException e) {
            String regex = "^\\d{4}-\\d{2}-\\d{2}/\\d{2}-\\d{2}-\\d{2}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);

            if (!matcher.matches()) {
                return "Formato de data inválido. Por favor use: `0000-00-00/00-00-00`, sendo eles `ano-mês-dia/hr-min-sec`.\nPor exemplo: `2013-11-29/14-53-06` é o mesmo que `29/11/2013 às 14:53:06`.";
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH-mm-ss");
            LocalDateTime date = LocalDateTime.parse(str, formatter);
            
            return "`(UTC)`\n" + date.toEpochSecond(ZoneOffset.UTC);
        }
    }

    private String emoji(String[] array) {
        StringBuilder builder = new StringBuilder();

        for (String s : array) {
            try {
                int num = Integer.parseInt(s);

                switch (String.valueOf(num)) {
                    case "0" -> builder.append(":zero: ");
                    case "1" -> builder.append(":one: ");
                    case "2" -> builder.append(":two: ");
                    case "3" -> builder.append(":three: ");
                    case "4" -> builder.append(":four: ");
                    case "5" -> builder.append(":five: ");
                    case "6" -> builder.append(":six: ");
                    case "7" -> builder.append(":seven: ");
                    case "8" -> builder.append(":eight: ");
                    case "9" -> builder.append(":nine: ");

                    // This is probably something unreachable but just to be sure lol
                    default -> builder.append(" ");
                }
            } catch (NumberFormatException e) {
                String regex = "[a-zA-Z]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                String emoji = ":regional_indicator_";

                if (!matcher.matches()) {
                    builder.append(s).append(" ");
                    continue;
                }

                if (builder.length() > Message.MAX_CONTENT_LENGTH) {
                    return "O resultado final terá mais de `" + Message.MAX_CONTENT_LENGTH + "` caracteres por limitação do Discord eu não posso enviar mensagens maiores do que este valor.";
                }

                builder
                        .append(emoji)
                        .append(s.toLowerCase())
                        .append(": ");
            }
        }

        return builder.toString().stripTrailing();
    }
}