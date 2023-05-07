package bot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Randomize implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();

        if (member == null) return;

        if (args.length < 3) {
            Bot.sendGhostMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            message.delete().queue();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "double", "d" -> channel.sendMessage(doubleNum(args[2], true)).queue();
        
            case "integer", "int", "i" -> channel.sendMessage(doubleNum(args[2], false)).queue();

            default -> channel.sendMessage("Nenhuma ").queue();
        }

        message.delete().queue();
    }

    private String doubleNum(String str, boolean hasDecimal) {
        String regex = "-?\\d+(\\.\\d+)?--?\\d+(\\.\\d+)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (!matcher.matches())
            return "Padrão inválido. Por favor forneça: `start-end`. Ou seja, se o numero for entre 5 e 13, coloque `5-13`.\nLembrando, esses numeros são inclusivos, o valor pode ir de 5 até 13.";

        String[] nums = str.split("-");

        double start;
        double end;

        try {
            start = Double.parseDouble(nums[0]);
            end = Double.parseDouble(nums[1]);
        } catch (NumberFormatException e) {
            return "Não foi possível formatar os numeros fornecidos, tente novamente.";
        }

        double random = Math.random() * (end - start) + start;

        String result = hasDecimal
                ? String.valueOf(random)
                : String.valueOf((int) random);

        return "Resultado: `" + result + "`.";
    }
}