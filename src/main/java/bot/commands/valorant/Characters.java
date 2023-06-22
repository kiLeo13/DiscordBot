package bot.commands.valorant;

import java.awt.Color;
import java.util.List;

import bot.internal.abstractions.BotCommand;
import com.google.gson.Gson;

import bot.util.Bot;
import bot.util.content.Messages;
import bot.internal.abstractions.annotations.CommandPermission;
import bot.internal.managers.requests.RequestManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

@CommandPermission()
public class Characters extends BotCommand {
    private static final RequestManager requester = RequestManager.create();

    public Characters(String name) {
        super(true, name);
    }

    @Override
    public void run(@NotNull Message message, String[] args) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        String content = message.getContentRaw();

        if (args.length < 1) {
            channel.sendMessage(Messages.ERROR_TOO_FEW_ARGUMENTS.message()).queue();
            return;
        }

        List<Agent> agents = fetchAgents();

        Agent agent = resolveAgent(args[0], agents);

        // Does the agent exist?
        if (agent == null) {
            Bot.tempMessage(channel, "Agent não encontrado na API.", 10000);
            return;
        }

        MessageCreateBuilder send = new MessageCreateBuilder();

        send.setEmbeds(embed(agent, content.toLowerCase().endsWith("--full")));
        send.setContent(member.getAsMention());

        channel.sendMessage(send.build()).queue();
    }

    private MessageEmbed embed(Agent agent, boolean hasDescription) {
        EmbedBuilder builder = new EmbedBuilder();

        Color color = hexToRgb(agent.backgroundGradientColors[0]);
        String name = agent.displayName;
        String description = agent.description;
        String image = agent.displayIcon;
        List<Ability> abilities = agent.abilities;

        builder
                .setTitle(name)
                .setDescription(description)
                .setThumbnail(image)
                .addField("✨ Habilidades", String.format("""
                        `%s`
                        `%s`
                        `%s`
                        `%s`
                        """,
                        abilities.get(0).displayName,
                        abilities.get(1).displayName,
                        abilities.get(2).displayName,
                        abilities.get(3).displayName), false)
                .setImage(agent.bustPortrait)
                .setColor(color);

        if (hasDescription) {
            builder
                .addField(abilities.get(0).displayName, abilities.get(0).description, false)
                .addField(abilities.get(1).displayName, abilities.get(1).description, false)
                .addField(abilities.get(2).displayName, abilities.get(2).description, false)
                .addField(abilities.get(3).displayName, abilities.get(3).description, false);
        }

        return builder.build();
    }

    private Agent resolveAgent(String arg, List<Agent> agents) {
        for (Agent a : agents)
            if (a.displayName.equalsIgnoreCase(arg) && a.isPlayableCharacter)
                return a;

        return null;
    }

    private List<Agent> fetchAgents() {
        String response = requester.requestAsString("https://valorant-api.com/v1/agents/", null);
        Gson gson = new Gson();
        return gson.fromJson(response, Agents.class).data;
    }

    private Color hexToRgb(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            int a = Integer.parseInt(hex.substring(6, 8), 16);

            return new Color(r, g, b, a);
        } catch (NumberFormatException ignore) {}

        return null;
    }

    private record Agents(List<Agent> data) {}

    private record Agent(
            String displayName,
            String displayIcon,
            String description,
            String bustPortrait,
            String[] backgroundGradientColors,
            boolean isPlayableCharacter,
            List<Ability> abilities
    ) {}

    private record Ability(String displayName, String description) {}
}