package bot;

import bot.commands.*;
import bot.commands.misc.PayServer;
import bot.data.BotConfig;
import bot.events.CommandHandler;
import bot.events.MessageReceivedGeneral;
import bot.events.SlashHandler;
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
            api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Oficina"), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        }

        // Registers
        registerEvents(api);
        registerCommands();
        runRunnables(api);
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents(JDA api) {
        api.addEventListener(CommandHandler.getInstance());
        api.addEventListener(new MessageReceivedGeneral());
        api.addEventListener(SlashHandler.getInstance());
    }

    private static void registerCommands() {
        CommandHandler commands = CommandHandler.getInstance();

        commands.addListenerCommand("<default>bigo", new BigoAnnouncement());
        commands.addListenerCommand("<default>disconnectall", new DisconnectAll());
        commands.addListenerCommand("<default>disconnect", new Disconnect());
        commands.addListenerCommand("<default>ping", new Ping());
        commands.addListenerCommand("<default>puta", new Puta());
        commands.addListenerCommand("<default>among", new RoleAmongUs());
        commands.addListenerCommand("<default>say", new Say());
        commands.addListenerCommand("<default>uptime", new Uptime());
        commands.addListenerCommand("<default>voicemoveall", new VoiceMoveAll());

        commands.addListenerCommand("<register>roles", new RegistrationRoles());
        commands.addListenerCommand("<register>take", new RegistrationTake());
    }

    private static void updateCommands(JDA jda) {
        SlashHandler slash = SlashHandler.getInstance();
        List<CommandData> commands = new ArrayList<>();

        // Disconnect
        commands.add(Commands.slash("disconnect", "Disconnects the player from the current voice-channel."));

        // Ping
        commands.add(Commands.slash("ping", "Sends you the ping.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        // Disconnectall
        OptionData disonnectAllChannels = new OptionData(OptionType.CHANNEL, "channel", "Decides which channel should players be disconnected from.", true)
                .setChannelTypes(ChannelType.VOICE);
        OptionData disconnectAllChannelsOptions = new OptionData(OptionType.STRING, "filter", "Filters the members to be disconnected.", false)
                .addChoice("Staff", "staff")
                .addChoice("Eventos", "eventos")
                .addChoice("Rádio", "radio")
                .addChoice("Rádio & Eventos", "both");

        commands.add(Commands.slash("disconnectall", "Disconnects every user from a voice-channel (with filtering feature).")
                .addOptions(disonnectAllChannels, disconnectAllChannelsOptions));

        // Registration
        OptionData registrationGender = new OptionData(OptionType.STRING, "gender", "O gênero do membro a ser registrado.", true)
                .addChoice("Feminino", "female")
                .addChoice("Masculino", "male")
                .addChoice("Não binário", "nonBinary");

        OptionData registrationAge = new OptionData(OptionType.INTEGER, "age", "A idade do membro a ser registrado.", true);

        OptionData registrationTarget = new OptionData(OptionType.USER, "target", "O membro a ser registrado.", true);

        OptionData registrationPlataform = new OptionData(OptionType.STRING, "plataform", "A plataforma que o membro a ser registrado usa o Discord.", true)
                .addChoice("Computador 💻", "pc")
                .addChoice("Mobile 📱", "mobile");

        commands.add(Commands.slash("register", "Registers a new member.")
                .addOptions(registrationGender, registrationAge, registrationPlataform, registrationTarget)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        jda.updateCommands().addCommands(commands).queue();

        // Internally register all the slash commands
        slash.addListenerCommand("disconnect", new Disconnect());
        slash.addListenerCommand("ping", new Ping());
        slash.addListenerCommand("disconnectall", new DisconnectAll());
        slash.addListenerCommand("register", Registration.getInstance());
    }

    public static long getInitTime() {
        return init;
    }

    private static void runRunnables(JDA api) {
        // Pay Server
        PayServer server = new PayServer(api);
        server.start();
    }
}