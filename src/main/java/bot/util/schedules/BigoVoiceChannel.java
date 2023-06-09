package bot.util.schedules;

import java.util.List;
import bot.Main;
import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Voices;
import bot.util.interfaces.BotScheduler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class BigoVoiceChannel implements BotScheduler {

    @Override
    public void perform() {

        VoiceChannel saladaVC = Main.getApi().getVoiceChannelById(Voices.SALADA.id());
        TextChannel salada = Main.getApi().getTextChannelById(Channels.SALADA.id());
        String bigo = "974159685764649010";

        if (saladaVC == null || salada == null) return;

        final List<Member> members = saladaVC.getMembers();

        // Just ignore it if Bigo is already connected
        if (members.stream().map(Member::getId).toList().contains(bigo)) return;

        if (members.size() >= 5)
            Bot.tempMessage(salada, "<@974159685764649010> acabei de verificar e é melhor vc não entrar na call, tem `" + members.size() + "` pessoas lá :fearful:", 300000);

        if (members.size() == 4)
            Bot.tempMessage(salada, "<@974159685764649010> tem mais 1 vaga para a call, dá para vc entrar hein.", 300000);

        if (members.size() <= 3 && members.size() != 1)
            Bot.tempMessage(salada, "<@974159685764649010> tá de boa para entrar, tem só `" + members.size() + "` pessoas na call.", 300000);

        if (members.size() == 1)
            Bot.tempMessage(salada, "<@974159685764649010> vai lá fazer companhia pro cara, tem só ele ou ela na call, sei lá", 300000);
    }
}