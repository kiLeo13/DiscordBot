package bot.commands.colorroles;

import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import bot.internal.abstractions.SlashExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ColorRole implements SlashExecutor {
    private static final int REMOVAL = 60;
    protected static final File file = new File("resources", "colors.json");
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void process(@NotNull SlashCommandInteraction event) {

        event.reply("Command under maintenance.").queue();

        Member member = event.getMember();
        Guild guild = event.getGuild();
        Member target = event.getOption("member").getAsMember();
        Role color = guild.getRoleById(event.getOption("color").getAsString());

        // Pretty sure it's impossible to happen but well, better safe than sorry?
        if (target == null) {
            event.reply(Messages.ERROR_MEMBER_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        if (color == null) {
            event.reply(Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        guild.addRoleToMember(target, color).queue(s -> {
            TextChannel bank = guild.getTextChannelById(Channels.CHANNEL_BANK.id());
            store(member, target, color);

            event.reply("Cargo `" + color.getName() + "` foi adicionado/atualizado com sucesso à <@" + target.getId() + ">.")
                    .setEphemeral(true)
                    .queue();

            String inform = target.getRoles().contains(color)
                    ? target.getAsMention() + " Você já possuia o cargo `"+ color.getName() + "`! Data de remoção adiada para `" + REMOVAL + " dias`."
                    : target.getAsMention() + " Cargo de cor `" + color.getName() + "` foi adicionado com sucesso.";
            
            if (bank != null)
                bank.sendMessage(inform).queue();
        }, e -> {
            event.reply("Não foi possível completar a operação, verifique o console para mais informações sobre o problema.")
                    .setEphemeral(true)
                    .queue();
            e.printStackTrace();
        });
    }

    private void store(Member staff, Member target, Role role) {
        String json = Bot.read(file);
        long now = System.currentTimeMillis();
        final Map<String, Colors> persons = new HashMap<>(convert(json));
        final List<UserData> data = new ArrayList<>();

        Colors currentColor = persons.get(role.getId());
        if (currentColor != null)
            data.addAll(currentColor.data);

        UserData userData = new UserData(
                role.getGuild().getId(),
                target.getId(),
                staff.getId(),
                now,
                now * 86400000 * REMOVAL // Plus 60 days
        );

        data.add(userData);

        Colors colors = new Colors(data);

        persons.put(role.getId(), colors);

        String write = gson.toJson(persons);
        Bot.write(write, file);
    }

    protected static Map<String, Colors> convert(String json) {
        final TypeToken<Map<String, Colors>> token = new TypeToken<>(){};
        Map<String, Colors> persons = gson.fromJson(json, token.getType());

        return persons == null
                ? Map.of()
                : Collections.unmodifiableMap(persons);
    }

    protected record Colors(
            List<UserData> data
    ) {}

    protected record UserData(
            String guild,
            String member,
            String staff,
            long added,
            long removal
    ) {}
}