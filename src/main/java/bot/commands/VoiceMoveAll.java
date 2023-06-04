package bot.commands;

import bot.util.content.Messages;
import bot.util.interfaces.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;

public class VoiceMoveAll implements SlashExecutor {

    @Override
    public void process(SlashCommandInteractionEvent event) {
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
        event.reply("Todos os membros de `#" + initChannel.getName() + "` agora estão em `#" + finalChannel.getName() + "`!")
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void help(Message message) {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = message.getGuild();

        builder
                .setColor(Color.YELLOW)
                .setTitle("Voice Move All", guild.getIconUrl())
                .setDescription("Segue uma explicação mais detalhada sobre o comando fornecido.")
                .addField("> 📝 Requisitos", "Atualmente para executar este comando, requer `Permission.MANAGE_SERVER` em um dos seus cargos.", true)
                .addField("> ❓ O que é", "É um comando feito para mover todos os usuários de um canal de voz para outro sem exceções.", true)
                .addField("> ❗ Disclaimer", "Tenha em mente que pode demorar um pouco para mover todos os membros devido ao rate-limit do Discord. Saiba mais: https://discord.com/developers/docs/topics/rate-limits", true)
                .setFooter("Oficina Myuu", guild.getIconUrl());

        message.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}