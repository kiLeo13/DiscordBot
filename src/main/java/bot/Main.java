package bot;

import bot.commands.Shutdown;
import bot.commands.*;
import bot.commands.tickets.*;
import bot.commands.valorant.*;
import bot.internal.managers.commands.*;
import bot.internal.data.*;
import bot.misc.features.*;
import bot.misc.schedules.*;
import bot.util.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
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
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.SCHEDULED_EVENTS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS
                    )
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
                new Links(),
                new OnBotPing(),
                new WordFilter(),
                CommandHandler.getManager(),
                SlashHandler.getManager(),
                new MessageInputTicket(),

                // Modals
                new TicketInfoCreation(),
                new TicketClosedReason()
        );
    }

    private static void registerCommands() {
        final CommandHandler manager = CommandHandler.getManager();

        manager.registerCommands(
                new Ping("{pf}ping"),
                new RoleAmongUs("{pf}among"),
                new Say("{pf}say"),
                new Uptime("{pf}uptime"),
                new Clear("{pf}clear"),
                new Userinfo("{pf}userinfo"),
                new Avatar("{pf}avatar"),
                new Help("{pf}help"),
                new Banner("{pf}banner"),
                new ServerInfo("{pf}serverinfo"),
                new IPLookup("{pf}ip"),
                new RoleInfo("{pf}roleinfo"),
                new Tumaes("{pf}tumaes", "{pf}anão", "{pf}toquinho", "{pf}sacy"),
                new Permissions("{pf}permissions", "{pf}permission"),
                new Characters("{pf}v-agent"),
                new Profiles("{pf}v-player"),
                new Disconnect("{pf}dd", "{pf}disconnect"),
                new RegistrationTake("{pf}reg-take")
        );

        Bot.log("{GREEN}Successfully registered {YELLOW}" + CommandHandler.getCommands().size() + "{GREEN} commands!");
        registerApplicationCommands();
    }

    private static void registerApplicationCommands() {
        SlashHandler slash = SlashHandler.getManager();
        List<CommandData> commands = new ArrayList<>();

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

        /* -------------------- TICKET SYSTEM -------------------- */
        OptionData closeTicketIsRefused = new OptionData(OptionType.BOOLEAN, "refused", "Determina se o ticket foi fechado porque foi recusado a ser respondido.", false);
        commands.add(Commands.slash("ticket", "Abre um novo ticket para entrar em contato com nossa equipe."));

        commands.add(Commands.slash("close", "Feche o ticket atual.")
                .addOptions(closeTicketIsRefused)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));

        // Registering it
        api.updateCommands().addCommands(commands).queue(m -> Bot.log("{GREEN}Successfully registered {YELLOW}" + commands.size() + "{GREEN} slash commands!"),e -> {
            e.printStackTrace();
            Bot.log("{RED}Could not register commands.");
        });

        // Internally register all the slash commands
        slash
                .register("shutdown", new Shutdown())
                .register("close", new CloseTicket())
                .register("ticket", new OpenTicket());
    }

    public static long getInitTime() {
        return init;
    }

    private static void runRunnables() {
        final ScheduleManager scheduler = ScheduleManager.getManager();

        scheduler
                .addRunnable(3600 * 1000, new PayServer());

        // Starts the schedule
        scheduler.release();
    }
}