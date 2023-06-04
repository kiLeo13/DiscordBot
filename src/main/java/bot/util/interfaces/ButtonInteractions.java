package bot.util.interfaces;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonInteractions {
    void perform(ButtonInteractionEvent event);
}