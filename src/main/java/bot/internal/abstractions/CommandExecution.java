package bot.internal.abstractions;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

interface CommandExecution {

    /**
     * Runs the code corresponding to the selected command.
     *
     * @param message The {@link Message} that triggered the command.
     * @param args The arguments of the command or an empty array if no arguments were prvided.
     */
    void run(Message message, String[] args);

    /**
     * Provides helpful information about a command.
     *
     * @return A {@link MessageEmbed} with the information or null if no help.
     */
    default MessageEmbed help() {
        return null;
    }
}