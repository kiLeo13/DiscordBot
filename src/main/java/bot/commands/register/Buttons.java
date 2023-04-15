package bot.commands.register;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Buttons extends ListenerAdapter {

    @SubscribeEvent
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        // Nothing for now lol
    }
}