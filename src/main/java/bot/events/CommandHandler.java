package bot.events;

import bot.commands.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getGuild().getIdLong() != 582430782577049600L) return;
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String contentLC = message.getContentRaw().toLowerCase();

        switch (contentLC) {
            // case ".crole" -> ColorRoleSchedule.run(message);

            case ".disconnect" -> Disconnect.run(message);

            case ".ping" -> Ping.run(message);

            case ".bigo" -> BigoAnnouncement.run(message);

            case "r!roles" -> RegistrationRoles.run(message);

            case "r!help" -> Registration.help(message);
        }

        if (contentLC.startsWith(".say")) Say.speak(message);
        if (contentLC.startsWith(".help")) HelpManager.run(message);
        if (contentLC.startsWith(".among")) RoleAmongUs.run(message);
        if (contentLC.startsWith(".uptime")) Uptime.run(message);
        if (contentLC.startsWith(".disconnectall")) DisconnectAll.run(message);
        if (contentLC.startsWith(".moveall")) VoiceMoveAll.run(message);
        if (contentLC.startsWith(".puta")) Puta.run(message);

        // Register related
        if (contentLC.startsWith("r!take")) RegistrationTake.run(message);
        if (contentLC.startsWith("r!") && !contentLC.startsWith("r!take")) {
            Registration.run(message);
        }
    }
}