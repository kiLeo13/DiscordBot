package bot.util.schedules;

import bot.Main;
import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Voices;
import bot.util.interfaces.BotScheduler;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class BigoVoiceChannel implements BotScheduler {

    @Override
    public void perform() {

        VoiceChannel saladaVC = Main.getApi().getVoiceChannelById(Voices.SALADA.id());
        TextChannel salada = Main.getApi().getTextChannelById(Channels.SALADA.id());

        if (saladaVC == null || salada == null) return;

        int members = saladaVC.getMembers().size();

        if (members >= 5)
            Bot.tempMessage(salada, "<@974159685764649010> acabei de verificar e é melhor vc não entrar na call, tem `" + members + "` pessoas lá :fearful:", 300000);

        if (members == 4)
            Bot.tempMessage(salada, "<@974159685764649010> tem mais 1 vaga para a call, dá para vc entrar hein.", 300000);

        if (members <= 3 && members != 1)
            Bot.tempMessage(salada, "<@974159685764649010> tá de boa para entrar, tem só `" + members + "` pessoas na call.", 300000);

        if (members == 1)
            Bot.tempMessage(salada, "<@974159685764649010> vai lá fazer companhia pro cara, tem só ele ou ela na call, sei lá", 300000);
    }
}