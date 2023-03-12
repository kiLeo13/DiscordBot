import data.BotConfig;
import events.MessageReceived;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        JDA jda = JDABuilder.createDefault(BotConfig.getToken())
                .build()
                .awaitReady();

        jda.addEventListener(new MessageReceived());
    }
}