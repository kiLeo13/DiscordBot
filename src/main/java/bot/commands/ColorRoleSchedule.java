package bot.commands;

import bot.util.Bot;
import bot.util.Channels;
import bot.util.Messages;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ColorRoleSchedule implements SlashExecutor {

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        // Usage: /color <user> <color>
        User author = event.getUser();
        Guild guild = event.getGuild();
        Member target = event.getOption("member").getAsMember();
        Role color = guild.getRoleById(event.getOption("color").getAsString());
        TextChannel logChannel = guild.getTextChannelById(Channels.LOG_COLOR_ROLE_COMMAND_CHANNEL.toId());

        if (target == null) {
            event.reply(Messages.ERROR_MEMBER_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        if (color == null) {
            event.reply(Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        // Guild related
        guild.addRoleToMember(target, color).queue();
        event.reply("// ---------- > INSERT A MESSAGE HERE <---------- //").setEphemeral(false).queue();
        Bot.sendGhostMessage(guild.getTextChannelById(Channels.CHANNEL_BANK.toId()), "<@" + target.getIdLong() + "> cargo `" + color.getName() + "` foi adicioando com sucesso.", 60000);

        String logMessage = logMessage("""
                Staff: `<author-name>#<author-discriminator>` `<authorId>`
                Membro: `<target-name>#<target-discriminator>` - `<targetId>`
                Cargo: `<role-name>`
                
                Data: `<date>`
                Data de Remoção: `<date-remove>`
                """, author, target.getUser(), color);

        // Log related
        System.out.println("Staff " + author.getName() + "#" + author.getName() + " deu o cargo `" + color.getName() + "` para " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + ".");

        if (logChannel != null) logChannel.sendMessage(logMessage).queue();
        else System.out.println("Não foi possível encontrar o canal de registros para cargo de cor, ignorando...");


    }

    private static String logMessage(String str, User author, User target, Role color) {
        final HashMap<String, String> placeHolder = new HashMap<>();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh/mm a");

        placeHolder.put("<author-name>", author.getName());
        placeHolder.put("<author-discriminator>", author.getDiscriminator());
        placeHolder.put("<author-id>", author.getId());

        placeHolder.put("<target-name>", target.getName());
        placeHolder.put("<target-discriminator>", target.getDiscriminator());
        placeHolder.put("<target-id>", target.getId());

        placeHolder.put("<date>", date.format(dateFormatter) + " às " + date.format(timeFormatter));
        placeHolder.put("<date-remove>", date.plusDays(60).format(dateFormatter) + " às " + date.plusDays(60).format(timeFormatter));

        placeHolder.put("<role-name>", color.getName());

        for (String i : placeHolder.keySet())
            str = str.replaceAll(i, placeHolder.get(i));

        return str;
    }
}