package bot.commands;

import bot.util.Channels;
import bot.util.ColorRoles;
import bot.util.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ColorRoleSchedule {
    private ColorRoleSchedule() {}

    public static void run(Message message) {

        // Usage: .color <user> <color>

        User author = message.getAuthor();
        Member member = message.getMember();
        String content = message.getContentRaw();
        Guild guild = message.getGuild();
        MessageChannelUnion channel = message.getChannel();
        TextChannel logChannel = guild.getTextChannelById(Channels.LOG_COLOR_ROLE_COMMAND_CHANNEL.toId());

        String[] args = content.split(" ");
        Member target = fetchTarget(args[1], guild);
        Role color = fetchColor(args[2], guild);

        if (!Channels.COMMAND_COLOR_ROLE_CHANNELS.toIds().contains(channel.getIdLong())) return;

        if (member == null || !member.hasPermission(Permission.MANAGE_ROLES)) return;
        if (author.isBot()) return;

        if (color == null) {
            System.out.println("Staff " + author.getName() + "#" + author.getDiscriminator() +
                    "`" + args[2] + "` digitou o nome de um cargo de cor incorretamente (" + args[2] + ").");

            message.delete().queue();
            Bot.sendExpireMessage(channel,
                    "<@" + author.getId() + "> could not find `" + args[2] + "`, did you type it correctly?",
                    10000);
            return;
        }

        if (target == null) {
            message.delete().queue();
            Bot.sendExpireMessage(channel,
                    "<@" + author.getId() + "> user `" + args[1] + "` not found.",
                    5000);
            return;
        }

        // Guild related
        guild.addRoleToMember(target, color).queue();
        message.delete().queue();
        Bot.sendExpireMessage(channel,
                "<@" + author.getId() + "> ",
                20000);

        String logMessage = logMessage("""
                Staff: `<author-name>#<author-discriminator>` `<authorId>`
                Membro: `<target-name>#<target-discriminator>` - `<targetId>`
                Cargo: `<role-name>`
                
                Data: `<date>` às `<time>`
                Data de Remoção: `<date-remove>` às `<time-remove>`
                """, author, target.getUser(), color);

        // Log related
        System.out.println("Staff " + author.getName() + "#" + author.getName() + " deu o cargo `" + color.getName() + "` para " + target.getEffectiveName() + "#" + target.getUser().getDiscriminator() + ".");

        if (logChannel != null) logChannel.sendMessage(logMessage).queue();
        else System.out.println("Não foi possível encontrar o canal de registros para cargo de cor, ignorando...");


    }

    private static Member fetchTarget(String arg, Guild guild) {
        arg = arg.replaceAll("[^0-9]+", "");

        return guild.retrieveMemberById(arg).complete();
    }

    private static Role fetchColor(String arg, Guild guild) {
        arg = arg.toLowerCase()
                .replaceAll("[^A-Za-z]", "");

        switch (arg) {
            case "fire", "fireelement" -> {
                return guild.getRoleById(ColorRoles.FIRE_ELEMENT.toLong());
            }

            case "earth", "earthelement" -> {
                return guild.getRoleById(ColorRoles.EARTH_ELEMENT.toLong());
            }

            case "water", "waterelement" -> {
                return guild.getRoleById(ColorRoles.WATER_ELEMENT.toLong());
            }

            case "light", "lightelement" -> {
                return guild.getRoleById(ColorRoles.LIGHT_ELEMENT.toLong());
            }

            case "air", "airelement" -> {
                return guild.getRoleById(ColorRoles.AIR_ELEMENT.toLong());
            }

            default -> {
                return null;
            }
        }
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

        placeHolder.put("<date>", date.format(dateFormatter));
        placeHolder.put("<time>", date.format(timeFormatter));

        placeHolder.put("<date-remove>", date.plusDays(30).format(dateFormatter));
        placeHolder.put("<time-remove>", date.plusDays(30).format(timeFormatter));

        placeHolder.put("<role-name>", color.getName());

        for (String i : placeHolder.keySet())
            str = str.replaceAll(i, placeHolder.get(i));

        return str;
    }
}