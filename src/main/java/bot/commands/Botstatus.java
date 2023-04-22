package bot.commands;

import java.time.LocalDateTime;

import bot.Main;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Botstatus implements SlashExecutor {

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        String input;
        String name;
        JDA api = Main.getApi();
        LocalDateTime now = LocalDateTime.now();

        String hour = now.getHour() < 10 ? "0" + now.getHour() : String.valueOf(now.getHour());
        String minute = now.getMinute() < 10 ? "0" + now.getMinute() : String.valueOf(now.getMinute());
        String second = now.getSecond() < 10 ? "0" + now.getSecond() : String.valueOf(now.getSecond());

        Member member = event.getMember();

        try {
            input = event.getOption("link").getAsString();
            name = event.getOption("name") == null ? null : event.getOption("name").getAsString();
        } catch (NullPointerException e) {
            input = null;
            name = null;
        }

        if (input == null || name == null || input.equals("none") || name.equals("none")) {
            event.reply("Resetando status do bot.").setEphemeral(true).queue();
            api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Oficina"), false);
            System.out.printf("[%s:%s:%s]: %s resetou a ativdade do bot\n", hour, minute, second, member.getUser().getName());
            return;
        }

        if (!Activity.isValidStreamingUrl(input)) {
            event.reply("O link `" + input + "` não é válido.").setEphemeral(true).queue();
            return;
        }

        event.reply("Status do bot alterado para `" + input + "`!").setEphemeral(true).queue();
        api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.streaming(name, input));
        System.out.printf("[%s:%s:%s]: %s alterou a ativdade do bot para `%s - %s`\n", hour, minute, second, member.getUser().getName(), name, input);
    }
}