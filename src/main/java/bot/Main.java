package bot;

import bot.commands.Shutdown;
import bot.commands.*;
import bot.commands.lifetimemute.LifeMuteCommand;
import bot.commands.lifetimemute.Reactions;
import bot.commands.lifetimemute.VoiceJoin;
import bot.commands.valorant.Characters;
import bot.commands.valorant.Profiles;
import bot.data.BotData;
import bot.data.BotFiles;
import bot.generic_listeners.*;
import bot.tickets.*;
import bot.util.Bot;
import bot.util.schedules.BigoVoiceChannel;
import bot.util.schedules.PayServer;
import bot.util.schedules.ScheduleManager;
import bot.util.server.SimpleHttpServer;
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
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main {
    private static JDA api;
    private static long init;

    public static void main(String[] args) {
        try {
            BotFiles.loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            api = JDABuilder.createDefault(BotData.TOKEN,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .setEventManager(new AnnotatedEventManager())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                    .build()
                    .awaitReady();

            init = System.currentTimeMillis();
            api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Oficina"), false);
            runRunnables();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to login, exiting...");
            return;
        }
                
                
        // Run http server
        try {
            SimpleHttpServer.runServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Registers
        registerCommands();
        registerEvents();
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerEvents() {
        api.addEventListener(

                // Text
                new AgeFilter(),
                new BlockLorittaExploit(),
                new Links(),
                new OnBotPing(),
                new WordFilter(),
                CommandHandler.getManager(),
                SlashHandler.getManager(),
                new MessageInputTicket(),

                // Voice
                new VoiceJoin(),

                // Mist
                new Reactions(),

                // Modals
                new TicketInfoCreation(),
                new TicketClosedReason()
        );
    }

    private static void registerCommands() {
        final CommandHandler commands = CommandHandler.getManager().register("<prefix>retrieve", new Retriever());

        commands.register("<prefix>bigo", new BigoAnnouncement())
                .register("<prefix>ping", new Ping())
                .register("<prefix>nerd", new Nerd())
                .register("<prefix>among", new RoleAmongUs())
                .register("<prefix>say", new Say())
                .register("<prefix>uptime", new Uptime())
                .register("<prefix>clear", new Clear())
                .register("<prefix>userinfo", new Userinfo())
                .register("<prefix>avatar", new Avatar())
                .register("<prefix>help", new Help())
                .register("<prefix>banner", new Banner())
                .register("<prefix>serverinfo", new ServerInfo())
                .register("<prefix>linff", new Linff())
                .register("<prefix>ip", new IPLookup())
                .register("<prefix>avatar-bot", new AvatarBot())
                .register("<prefix>randomize", new Randomize())
                .register("<prefix>roleinfo", new RoleInfo())
                .register("<prefix>p-help", new PrivilegedHelp())
                .register("<prefix>lifemute", new LifeMuteCommand())
                .register(new Permissions(), "<prefix>permissions", "<prefix>permission", "<prefix>perms", "<prefix>perm")

        // BRUH
                .register(new Characters(), "<prefix>valorant-agent", "<prefix>v-agent")
                .register(new Profiles(), "<prefix>valorant-player", "<prefix>v-player")

                .register(new Disconnect(), "<prefix>dd", "<prefix>disconnect")

                .register("<register>roles", new RegistrationRoles())
                .register("<register>take", new RegistrationTake());

        Bot.log("<GREEN>Successfully registered <YELLOW>" + CommandHandler.getCommands().size() + "<GREEN> commands!", false);
        registerApplicationCommands();
    }

    private static void registerApplicationCommands() {
        SlashHandler slash = SlashHandler.getManager();
        List<CommandData> commands = new ArrayList<>();

        /* []====================[] Disconnect []====================[] */
        commands.add(Commands.slash("disconnect", "Desconecta o usuário do canal de voz atual."));

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

        /* []====================[] Bot Status []====================[] */
        OptionData activityOption = new OptionData(OptionType.STRING, "link", "O link da live.");
        OptionData activityName = new OptionData(OptionType.STRING, "name", "Define o nome da live para aparecer no perfil.");
        
        commands.add(Commands.slash("stream", "Define os status do bot.")
                .addOptions(activityOption, activityName)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* []====================[] Transfer Member Data []====================[] */
        OptionData transferFirstMember = new OptionData(OptionType.USER, "from", "De qual membro devemos pegar os cargos.", true);
        OptionData transferSecondMember = new OptionData(OptionType.USER, "to", "Para qual membro devemos adicionar os cargos.", true);
        OptionData transferIgnore = new OptionData(OptionType.STRING, "action", "Se devemos ignorar se o membro 'from' tem mais permissões que o outro que receberá os cargos.", false)
                .addChoice("Ignore", "ignore")
                .addChoice("Revert", "revert");

        commands.add(Commands.slash("transfer", "Transfere os cargos de um membro para outro (o membro anterior não perderá os cargos).")
                .addOptions(transferFirstMember, transferSecondMember, transferIgnore)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        /* -------------------- TICKET SYSTEM -------------------- */
        OptionData closeTicketIsRefused = new OptionData(OptionType.BOOLEAN, "refused", "Determina se o ticket foi fechado porque foi recusado a ser respondido.", false);
        commands.add(Commands.slash("ticket", "Abre um novo ticket para entrar em contato com nossa equipe."));

        commands.add(Commands.slash("close", "Feche o ticket atual.")
                .addOptions(closeTicketIsRefused)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        // Registering it
        api.updateCommands().addCommands(commands).queue(m -> {
                Bot.log("<GREEN>Successfully registered <YELLOW>" + commands.size() + "<GREEN> slash commands!", false);
        }, e -> {
                e.printStackTrace();
                Bot.log("<RED>Could not register commands.", true);
        });

        // Internally register all the slash commands
        slash
                .register("disconnect", new Disconnect())
                .register("disconnectall", new DisconnectAll())
                .register("register", Registration.getInstance())
                .register("moveall", new VoiceMoveAll())
                .register("color", new ColorRole())
                .register("shutdown", new Shutdown())
                .register("stream", new BotStatus())
                .register("close", new CloseTicket())
                .register("ticket", new OpenTicket())
                .register("transfer", new TransferMemberData());
    }

    public static long getInitTime() {
        return init;
    }

    private static void runRunnables() {
        final ScheduleManager scheduler = ScheduleManager.getManager();

        scheduler
                .addRunnable(3600 * 1000, new PayServer())
                .addRunnable(60000, new ColorRole())
                .addRunnable(3600 * 1000, new BigoVoiceChannel());

        // Starts the schedule
        scheduler.release();
    }
}