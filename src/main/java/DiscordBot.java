import data.BotConfig;
import events.MessageReceived;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {
    private static JDA api;

    public static void main(String[] args) {

        try {
            api = JDABuilder.createDefault(BotConfig.getToken(),
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setEventManager(new AnnotatedEventManager())
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        }

        registerEvents(api);
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents(JDA api) {
        api.addEventListener(new MessageReceived());
    }
}