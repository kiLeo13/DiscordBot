package bot;

import bot.commands.Shutdown;
import bot.commands.*;
import bot.commands.misc.PayServer;
import bot.commands.valorant.Characters;
import bot.commands.valorant.Profiles;
import bot.data.BotData;
import bot.data.BotFiles;
import bot.events.*;
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

public final class Main {
    private static JDA api;
    private static long init;

    public static void main(String[] args) {
        try {
            BotFiles.createYamlFiles();
        } catch (IOException e) {
            System.out.println("Could not load YAML files");
        }

        try {
            api = JDABuilder.createDefault(BotData.getToken(),
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .setEventManager(new AnnotatedEventManager())
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build()
                    .awaitReady();

            registerApplicationCommands(api);

            init = System.currentTimeMillis();
            api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Oficina"), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        }

        // Registers
        registerEvents(api);
        registerOldCommands();
        runRunnables(api);
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents(JDA api) {
        api.addEventListener(new AgeFilter());
        api.addEventListener(new BlockLorittaExploit());
        api.addEventListener(CommandHandler.getInstance());
        api.addEventListener(new FormattedBlocker());
        api.addEventListener(SlashHandler.getInstance());
        api.addEventListener(new WordFilter());
    }

    private static void registerOldCommands() {
        CommandHandler commands = CommandHandler.getInstance();

        commands.addCommand("<prefix>bigo", new BigoAnnouncement());
        commands.addCommand("<prefix>ping", new Ping());
        commands.addCommand("<prefix>puta", new Puta());
        commands.addCommand("<prefix>nerd", new Nerd());
        commands.addCommand("<prefix>among", new RoleAmongUs());
        commands.addCommand("<prefix>say", new Say());
        commands.addCommand("<prefix>uptime", new Uptime());
        commands.addCommand("<prefix>clear", new Clear());
        commands.addCommand("<prefix>userinfo", new Userinfo());
        commands.addCommand("<prefix>avatar", new Avatar());
        commands.addCommand("<prefix>help", new Help());
        commands.addCommand("<prefix>banner", new Banner());
        commands.addCommand("<prefix>serverinfo", new ServerInfo());
        commands.addCommand("<prefix>linff", new Linff());
        commands.addCommand("<prefix>ip", new IPLookup());
        commands.addCommand("<prefix>avatar-bot", new AvatarBot());

        commands.addCommand(new Format(), "<prefix>format", "<prefix>parse");
        commands.addCommand(new Characters(), "<prefix>valorant-agent", "<prefix>v-agent");
        commands.addCommand(new Profiles(), "<prefix>valorant-player", "<prefix>v-player");

        commands.addCommand(new Disconnect(), "<prefix>dd", "<prefix>disconnect");

        commands.addCommand("<register>roles", new RegistrationRoles());
        commands.addCommand("<register>take", new RegistrationTake());
    }

    private static void registerApplicationCommands(JDA jda) {
        SlashHandler slash = SlashHandler.getInstance();
        List<CommandData> commands = new ArrayList<>();

        /* []====================[] Disconnect []====================[] */
        commands.add(Commands.slash("disconnect", "Desconecta o usuário do canal de voz atual."));

        /* []====================[] Ping []====================[] */
        commands.add(Commands.slash("ping", "Sends you the ping.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* []====================[] Disconnect All []====================[] */
        OptionData disonnectAllChannels = new OptionData(OptionType.CHANNEL, "channel", "Decide de qual canal os membros devem ser desconectados.", true)
                .setChannelTypes(ChannelType.VOICE);
        OptionData disconnectAllChannelsOptions = new OptionData(OptionType.STRING, "filter", "Filtra os membros a NÃO serem desconectados.", false)
                .addChoice("Staff ✩", "staff")
                .addChoice("Eventos 🎈", "eventos")
                .addChoice("Rádio 📻", "radio")
                .addChoice("Rádio & Eventos 🎤", "both");

        commands.add(Commands.slash("disconnectall", "Desconecta todos os membros de um canal de voz (opção de filtragem)")
                .addOptions(disonnectAllChannels, disconnectAllChannelsOptions)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        /* []====================[] Registration []====================[] */
        OptionData registrationGender = new OptionData(OptionType.STRING, "gender", "O gênero do membro a ser registrado.", true)
                .addChoice("Feminino", "female")
                .addChoice("Masculino", "male")
                .addChoice("Não binário", "nonBinary");

        OptionData registrationAge = new OptionData(OptionType.INTEGER, "age", "A idade do membro a ser registrado.", true);

        OptionData registrationTarget = new OptionData(OptionType.USER, "target", "O membro a ser registrado.", true);

        OptionData registrationPlataform = new OptionData(OptionType.STRING, "plataform", "A plataforma que o membro a ser registrado usa o Discord.", true)
                .addChoice("Computador 💻", "pc")
                .addChoice("Mobile 📱", "mobile");

        commands.add(Commands.slash("register", "Registra um novo membro.")
                .addOptions(registrationGender, registrationAge, registrationPlataform, registrationTarget)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        /* []====================[] Move All []====================[] */
        OptionData initialChannel = new OptionData(OptionType.CHANNEL, "init-channel", "Canal onde os membros atualmente estão.", true)
               .setChannelTypes(ChannelType.VOICE);

        OptionData finalChannel = new OptionData(OptionType.CHANNEL, "final-channel", "Canal para onde os membros irão ser movidos.", true)
                .setChannelTypes(ChannelType.VOICE);

        OptionData shouldOverride = new OptionData(OptionType.BOOLEAN, "should-ignore", "Define se o bot deve ignorar as limitações de chat, como por exemplo, limite de membros.", false);

        commands.add(Commands.slash("moveall", "Move todos os membros de um canal de voz para outro.")
                .addOptions(initialChannel, finalChannel, shouldOverride)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* []====================[] Color Role []====================[] */
        OptionData optionColorTarget = new OptionData(OptionType.USER, "member", "Qual membro deve receber o cargo de cor informado.", true);
        OptionData optionColorRole = new OptionData(OptionType.STRING, "color", "Qual o cargo de cor a ser dado ao membro infromado.", true)
                .addChoice("Fire Element \uD83D\uDD25", "946061433060347915")
                .addChoice("Earth Element \uD83C\uDF3F", "946061781284032612")
                .addChoice("Water Element \uD83D\uDCA7", "946062089011752960")
                .addChoice("Light Element \uD83C\uDF1F", "946063870903074816")
                .addChoice("Air Element \uD83D\uDCA8", "946064125916753960");

        commands.add(Commands.slash("color", "Dá o cargo de cor para o membro informado.")
                .addOptions(optionColorTarget, optionColorRole)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));

        /* []====================[] Shutdown []====================[] */
        commands.add(Commands.slash("shutdown", "Desliga o bot em caso de emergência.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* []====================[] Avatar []====================[] */
        OptionData targetAvatar = new OptionData(OptionType.USER, "user", "O avatar do usuário a ser enviado.", false);
        OptionData avatarGuild = new OptionData(OptionType.STRING, "from-guild", "O avatar a ser mostrado é o que está no servidor ou no perfil do usuário?", false)
                .addChoice("Do Servidor 🌎", "guild")
                .addChoice("Do Usuário 👥", "user");

        commands.add(Commands.slash("avatar", "Mostra o avatar do usuário.")
                .addOptions(targetAvatar, avatarGuild));

        /* []====================[] Banner []====================[] */
        OptionData targetBanner = new OptionData(OptionType.USER, "user", "O banner do usuário a ser enviado.", false);

        commands.add(Commands.slash("banner", "Mostra o banner do usuário.")
                .addOptions(targetBanner)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* []====================[] Bot Status []====================[] */
        OptionData activityOption = new OptionData(OptionType.STRING, "link", "O link da live.");
        OptionData activityName = new OptionData(OptionType.STRING, "name", "Define o nome da live para aparecer no perfil.");
        
        commands.add(Commands.slash("stream", "Define os status do bot.")
                .addOptions(activityOption, activityName)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        // Registering it
        jda.updateCommands().addCommands(commands).queue();

        // Internally register all the slash commands
        slash.addListenerCommand("disconnect", new Disconnect());
        slash.addListenerCommand("ping", new Ping());
        slash.addListenerCommand("disconnectall", new DisconnectAll());
        slash.addListenerCommand("register", Registration.getInstance());
        slash.addListenerCommand("moveall", new VoiceMoveAll());
        slash.addListenerCommand("color", new ColorRole());
        slash.addListenerCommand("shutdown", new Shutdown());
        slash.addListenerCommand("avatar", new Avatar());
        slash.addListenerCommand("banner", new Banner());
        slash.addListenerCommand("stream", new BotStatus());
    }

    public static long getInitTime() {
        return init;
    }

    private static void runRunnables(JDA api) {
        // Pay Server
        PayServer server = new PayServer(api);
        server.start();

        // Color Role runnable
        ColorRole.startCounter();
    }
}