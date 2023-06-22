package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.internal.abstractions.annotations.CommandPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;

@CommandPermission()
public class Help extends BotCommand {

    public Help(String name) {
        super(false, name);
    }

    @Override
    public void run(Message message, String[] args) {

        Member member = message.getMember();
        Guild guild = message.getGuild();
        MessageCreateBuilder send = new MessageCreateBuilder()
                .setContent(member.getAsMention());

        if (args.length == 0) {
            send.setEmbeds(getDefaultHelp(guild));
            return;
        }
    }

    private MessageEmbed getDefaultHelp(Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("📞 Ajuda Central")
                .setFooter(guild.getName(), guild.getIconUrl());

        return builder.build();
    }
}