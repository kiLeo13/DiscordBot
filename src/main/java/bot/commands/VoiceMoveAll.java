package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VoiceMoveAll implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        User author = message.getAuthor();
        Guild guild = message.getGuild();
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        List<VoiceChannel> vChannels = new ArrayList<>();

        if (author.isBot()) return;
        if (member == null || !member.hasPermission(Permission.MANAGE_SERVER)) return;

        try {
            String currentRegex = args[1].replaceAll("[^0-9]+", "");
            String futureRegex = args[2].replaceAll("[^0-9]+", "");

            vChannels.add(guild.getVoiceChannelById(currentRegex));
            vChannels.add(guild.getVoiceChannelById(futureRegex));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { vChannels.add(null); }

        for (VoiceChannel v : vChannels)
            if (v == null) {
                Bot.sendExpireMessage(channel, "<@" + author.getIdLong() + "> canal de voz não encontrado.", 5000);
                message.delete().queue();
                return;
            }

        int currentChannelMemberAmount = vChannels.get(0).getMembers().size();
        int futureChannelMemberAmount = vChannels.get(1).getMembers().size();

        // If the limit is 0 (no limit), set to Integer.MAX_VALUE
        int futureChannelMemberLimit = vChannels.get(1).getUserLimit() <= 0 ? Integer.MAX_VALUE : vChannels.get(1).getUserLimit();

        if (currentChannelMemberAmount == 0) {
            Bot.sendExpireMessage(channel, Messages.ERROR_VOICE_CHANNEL_EMPTY.message(), 5000);
            message.delete().queue();
            return;
        }

        if (futureChannelMemberAmount == futureChannelMemberLimit && !content.endsWith("--force")) {
            Bot.sendExpireMessage(channel, "<@" + author.getIdLong() + "> o canal de destino já está lotado, para ignorar este aviso adicione `--force` no fim do comando.", 10000);
            message.delete().queue();
            return;
        }

        if (currentChannelMemberAmount + futureChannelMemberAmount > futureChannelMemberLimit && !content.endsWith("--force")) {
            Bot.sendExpireMessage(channel, "<@" + author.getIdLong() + "> o canal de voz de destino irá passar do limite de usuários ao executar o comando, caso queira movê-los mesmo assim, adicione `--force` no fim do comando.", 10000);
            message.delete().queue();
            return;
        }

        if (vChannels.get(0).getIdLong() == vChannels.get(1).getIdLong()) {
            Bot.sendExpireMessage(channel, "<@" + author.getIdLong() + "> você forneceu o mesmo canal de voz nos dois argumentos.", 5000);
            message.delete().queue();
            return;
        }

        List<Member> inChannelMembers = vChannels.get(0).getMembers();

        inChannelMembers.forEach(m -> guild.moveVoiceMember(m, vChannels.get(1)).queue());

        channel.sendMessage("<@" + author.getIdLong() + "> todos os membros de `#" + vChannels.get(0).getName() + "` agora estão em `#" + vChannels.get(1).getName() + "`!").queue();
        message.delete().queue();
    }

    @Override
    public void runAsSlash(SlashCommandInteractionEvent event) {
        VoiceChannel initChannel = event.getOption("init-channel").getAsChannel().asVoiceChannel();
        VoiceChannel finalChannel = event.getOption("final-channel").getAsChannel().asVoiceChannel();
        boolean override = event.getOption("should-ignore") != null && event.getOption("should-ignore").getAsBoolean();

        // If the limit is 0 (no limit), set to Integer.MAX_VALUE
        int maxFinalChannel = finalChannel.getUserLimit() <= 0 ? Integer.MAX_VALUE : finalChannel.getUserLimit();

        if (initChannel.getIdLong() == finalChannel.getIdLong()) {
            event.reply("Você forneceu o mesmo canal de voz em ambos os argumentos.").setEphemeral(true).queue();
            return;
        }

        if (initChannel.getMembers().isEmpty() && !override) {
            event.reply(Messages.ERROR_VOICE_CHANNEL_EMPTY.message()).setEphemeral(true).queue();
            return;
        }

        if (finalChannel.getMembers().size() >= maxFinalChannel && !override) {
            event.reply("O canal de destino já está cheio. Caso queira ignorar este aviso e executar o comando mesmo assim, marque o argumento `should-ignore` como `True`.").setEphemeral(true).queue();
            return;
        }

        // Will the channel limit be exeeded when running the command?
        if (initChannel.getMembers().size() + finalChannel.getMembers().size() > maxFinalChannel && !override) {
            event.reply("Ao executar o comando, o canal de destino terá seu limite ultrapassado. Caso queira ignorar este aviso e executar o comando mesmo assim, marque o argumento `should-ignore` como `True`.").setEphemeral(true).queue();
            return;
        }

        List<Member> initMembers = initChannel.getMembers();

        initMembers.forEach(m -> event.getGuild().moveVoiceMember(m, finalChannel).queue());
        event.reply("Todos os membros de `#" + initChannel.getName() + "` agora estão em `#" + finalChannel.getName() + "`!").setEphemeral(false).queue();
    }

    @Override
    public void help(Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Voice Move All", guild.getIconUrl())
                .setDescription("Segue uma explicação mais detalhada sobre o comando fornecido.")
                .addField("> 📝 Requisitos", "Atualmente para executar este comando, requer `Permission.MANAGE_SERVER` em um dos seus cargos.", true)
                .addField("> ❓ O que é", "É um comando feito para mover todos os usuários de um canal de voz para outro sem exceções.", true)
                .addField("> ❗ Disclaimer", "Tenha em mente que pode demorar um pouco para mover todos os membros devido ao rate-limit do Discord. Saiba mais: https://discord.com/developers/docs/topics/rate-limits", true)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        channel.sendMessageEmbeds(builder.build()).queue();
    }
}