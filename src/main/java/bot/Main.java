package bot;

import bot.data.BotConfig;
import bot.events.Countdown;
import bot.events.Stickers;
import bot.events.MessageReceived;
import bot.events.RegisterComand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Main {
    private static JDA api;
    private static Map<String, List<String>> swearings;

    public static void main(String[] args) {
        try { createYamlFiles(); }
        catch (FileNotFoundException e) {
            System.out.println("Could not load YAML files.");
        }

        try {
            api = JDABuilder.createDefault(BotConfig.getToken(),
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.GUILD_PRESENCES)
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
        api.addEventListener(new RegisterComand());
        api.addEventListener(new Countdown());
        api.addEventListener(new Stickers());
    }

    public static Map<String, List<String>> getSwearings() { return swearings; }

    private static void createYamlFiles() throws FileNotFoundException {
        File swearingFile = new File("C:/Users/Leonardo/Downloads/Stuff/src/main/java/bot/config/swearings.yml");
        Yaml swearingYaml = new Yaml();

        // Load our swearing file
        swearings = swearingYaml.load(new FileInputStream(swearingFile));
    }
}