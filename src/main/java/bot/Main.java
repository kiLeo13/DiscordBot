package bot;

import bot.commands.misc.PayServer;
import bot.data.BotConfig;
import bot.events.CommandHandler;
import bot.events.MessageReceivedGeneral;
import bot.events.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bot.data.BotFiles.createYamlFiles;

public final class Main {
    private static JDA api;
    private static long init;

    public static void main(String[] args) {
        try { createYamlFiles(); }
        catch (IOException e) {
            System.out.println("Could not load YAML files.");
        }

        try {
            api = JDABuilder.createDefault(BotConfig.getToken(),
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.GUILD_PRESENCES)
                    .setEventManager(new AnnotatedEventManager())
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build()
                    .awaitReady();

            updateCommands(api);
            init = System.currentTimeMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        }

        runRunnables(api);
        profile(api);
        registerEvents(api);
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents(JDA api) {
        api.addEventListener(new CommandHandler());
        api.addEventListener(new MessageReceivedGeneral());
        api.addEventListener(new SlashCommand());
    }

    private static void updateCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();

        // Disconnect
        commands.add(Commands.slash("disconnect", "Disconnects the player from the current voice-channel."));

        // Ping
        commands.add(Commands.slash("ping", "Sends you the ping.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        // Disconnectall
        OptionData channel = new OptionData(OptionType.CHANNEL, "channel", "Decides which channel should players be disconnected from.", true)
                .setChannelTypes(ChannelType.VOICE);
        OptionData filter = new OptionData(OptionType.STRING, "filter", "Filters the members to be disconnected.", false)
                .addChoice("Staff", "staff")
                .addChoice("Eventos", "eventos")
                .addChoice("Rádio", "radio")
                .addChoice("Rádio & Eventos", "both");

        commands.add(Commands.slash("disconnectall", "Disconnects every user from a voice-channel (with filtering feature).")
                .addOptions(channel, filter));

        jda.updateCommands().addCommands(commands).queue();
    }

    public static long getInitTime() {
        return init;
    }

    private static void profile(JDA api) {
        api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Oficina"), false);
    }

    private static void runRunnables(JDA api) {
        // Pay Server
        PayServer server = new PayServer(api);
        server.start();
    }
}