package bot;

import bot.data.BotConfig;
import bot.events.MessageReceived;
import bot.events.RegisterComand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Main {
    private static JDA api;
    private static Map<String, List<String>> object;

    public static void main(String[] args) {
        File ymlFile = new File("C:/Users/Leonardo/Downloads/Stuff/src/main/java/bot/util/swearings.yml");
        Yaml yaml = new Yaml();

        try {
            api = JDABuilder.createDefault(BotConfig.getToken(),
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES)
                    .setEventManager(new AnnotatedEventManager())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build()
                    .awaitReady();

            // Load our swearing file
            object = yaml.load(new FileInputStream(ymlFile));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to find file.");
        }

        registerEvents(api);
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents(JDA api) {
        api.addEventListener(new MessageReceived());
        api.addEventListener(new RegisterComand());
    }

    public static Map<String, List<String>> getFileObject() { return object; }
}