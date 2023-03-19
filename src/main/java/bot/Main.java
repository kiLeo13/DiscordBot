package bot;

import bot.commands.music.MusicHandler;
import bot.data.BotConfig;
import bot.events.CommandHandler;
import bot.events.MessageReceivedGeneral;
import bot.events.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;

import static bot.data.BotFiles.createYamlFiles;

public final class Main {
    private static JDA api;

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
                    .build()
                    .awaitReady();

            updateCommands(api);
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
        api.addEventListener(new CommandHandler());
        api.addEventListener(new MessageReceivedGeneral());
        api.addEventListener(new SlashCommand());
        api.addEventListener(new MusicHandler());
    }

    private static void updateCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("disconnect", "Disconnects the player from the current voice-channel."),
                Commands.slash("ping", "Sends you the ping.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        ).queue();
    }
}