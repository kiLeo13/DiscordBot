package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.Messages;
import bot.util.SlashExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;

public class Avatar implements CommandExecutor, SlashExecutor {

    @Override
    public void run(Message message) {

        MessageChannelUnion channel = message.getChannel();
        Member member = message.getMember();
        String content = message.getContentRaw();
        String[] args = content.split(" ");
        Member target;
        MessageCreateBuilder send = new MessageCreateBuilder();

        if (member == null) return;

        try {
            String targetId = args[1].replaceAll("[^0-9]+", "");

            if (targetId.stripTrailing().equals("")) {
                Bot.sendGhostMessage(channel, "`" + args[1] + "` não é um id válido.", 5000);
                message.delete().queue();
                return;
            }

            target = message.getGuild().retrieveMemberById(targetId).complete();
        } catch (ArrayIndexOutOfBoundsException e) {
            target = member;
        } catch (ErrorResponseException e) {
            Bot.sendGhostMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 5000);
            message.delete().queue();
            return;
        }

        boolean isFromGuild = content.endsWith("--server");
        String avatarUrl = avatarUrl(target, isFromGuild);

        if (avatarUrl == null && content.endsWith("--server")) {
            Bot.sendGhostMessage(channel, "O usuário não possui um avatar específico para este servidor.", 5000);
            message.delete().queue();
            return;
        }

        send.addEmbeds(embed(avatarUrl, target));
        send.setContent(String.format("<@%d>", member.getIdLong()));

        channel.sendMessage(send.build()).queue();
        message.delete().queue();
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        boolean isFromGuild;
        User target;
        Guild guild = event.getGuild();

        try { isFromGuild = event.getOption("from-guild").getAsString().equals("guild"); }
        catch (NullPointerException e) { isFromGuild = false; }

        try { target = event.getOption("user").getAsUser(); }
        catch (NullPointerException e) {
            target = event.getUser();
        }

        if (isFromGuild && guild.retrieveMemberById(target.getIdLong()).complete().getAvatarUrl() == null) {
            event.reply("O usuário não possui um avatar específico para este servidor.").setEphemeral(true).queue();
            return;
        }

        String avatarUrl = avatarUrl(guild.retrieveMemberById(target.getIdLong()).complete(), isFromGuild);

        event.replyEmbeds(embed(avatarUrl, guild.retrieveMemberById(target.getIdLong()).complete())).setEphemeral(false).queue();
    }

    private MessageEmbed embed(String url, Member target) {
        final EmbedBuilder builder = new EmbedBuilder();
        String name = target.getUser().getName();

        // Embed related
        String title = "🖼 " + name;
        Color color = new Color(88, 101, 242);

        switch (target.getId()) {

            // Custom stuff for Anjo
            case "742729586659295283" -> {
                color = new Color(148, 0, 211);
                title = "\\🍑 " + name;
            }

            // Custom stuff for Myuu (main)
            case "183645448509194240" -> {
                color = new Color(194, 0, 0);
                title = "🍒 " + name;
            }

            // Custom stuff for Myuu (alt)
            case "727978798464630824" -> {
                color = new Color(255, 51, 243);
                title = "🍒 " + name;
            }
        }

        return builder
                .setTitle(title, url)
                .setDescription(String.format("Avatar de `%s`", target.getNickname()))
                .setColor(color) // Discord 'blue color'
                .setImage(url)
                .build();
    }

    private String avatarUrl(Member target, boolean fromGuild) {
        String avatar = fromGuild ? target.getAvatarUrl() : target.getUser().getAvatarUrl();

        // If avatar is null, well, then just return null lol
        return avatar == null ? null : avatar + "?size=2048";
    }
}