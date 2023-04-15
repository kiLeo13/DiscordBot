package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.TimeUnit;

public class DisconnectAfter implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        short time;
        Member target = args.length < 3 ? null : Bot.findMember(guild, args[2]);

        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        if (args.length < 3) {
            Bot.sendGhostMessage(channel, "Poucos argumentos. Uso: `/disconnectafter <time> <user>`", 10000);
            message.delete().queue();
            return;
        }

        try {
            time = Short.parseShort(args[1]);
        } catch (NumberFormatException e) {
            Bot.sendGhostMessage(channel, "Tempo inválido! Por favor, forneça um valor (em segundos) entre `01` e `32767`", 10000);
            message.delete().queue();
            return;
        }

        if (time < 0) {
            Bot.sendGhostMessage(channel, "Você não pode inserir um valor menor do que 0.", 10000);
            message.delete().queue();
            return;
        }

        if (target == null) {
            Bot.sendGhostMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000);
            message.delete().queue();
            return;
        }

        if (time == 0) {
            Bot.sendGhostMessage(channel, "Desconectando `" + target.getEffectiveName() + "`...", 10000);
            guild.kickVoiceMember(target).queue();
            message.delete().queue();
        } else {
            long now = (long) Math.floor(System.currentTimeMillis() / 1000.0);
            long when = now + time;
            int toDelete = time >= 10 ? 10000 : time * 1000;

            Bot.sendGhostMessage(channel, "Irei desconectar `" + target.getEffectiveName() + "` <t:" + when + ":R>.", toDelete - 500);
            message.delete().queue();
            voiceKick(target, time);
        }
    }

    @Override
    public void runSlash(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        Guild guild = event.getGuild();
        short time;

        try {
            time = (short) event.getOption("time").getAsInt();
        } catch (NumberFormatException e) {
            event.reply("Tempo inválido! Por favor, forneça um valor (em segundos) entre `01` e `32767`").setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply(Messages.ERROR_MEMBER_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        if (time == 0) {
            event.reply("Desconectando `" + target.getEffectiveName() + "`...").setEphemeral(true).queue();
            guild.kickVoiceMember(target).queue();
        } else {
            long now = (long) Math.floor(System.currentTimeMillis() / 1000.0);
            long when = now + time;

            event.reply("Irei desconectar `" + target.getEffectiveName() + "` <t:" + when + ":R>.").setEphemeral(true).queue();
            voiceKick(target, time);
        }
    }

    private void voiceKick(Member target, short delay) {
        target.getGuild()
                .kickVoiceMember(target)
                .queueAfter(delay, TimeUnit.SECONDS, null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_USER));
    }
}
