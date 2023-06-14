package bot.commands.colorroles;

import bot.util.Bot;
import bot.util.content.Messages;
import bot.util.interfaces.SlashExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColorRole implements SlashExecutor {
    public static final int REMOVAL = 60;
    private static final File file = new File("resources", "colors.json");
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void process(SlashCommandInteractionEvent event) {

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
            store(target, color);
            event.reply("Cargo `" + color.getName() + "` foi adicionado com sucesso à <@" + target.getId() + ">.")
                    .setEphemeral(true)
                    .queue();
        }, e -> {
            event.reply("Não foi possível completar a operação, verifique o console para mais informações sobre o problema.")
                    .setEphemeral(true)
                    .queue();
            e.printStackTrace();
        });
    }

    private void store(Member target, Role role) {
        String json = Bot.read(file);
        final Map<String, Data> persons = new HashMap<>(convert(json));


    }

    private Map<String, Data> convert(String json) {
        final TypeToken<Map<String, Data>> token = new TypeToken<>(){};
        Map<String, Data> persons = gson.fromJson(json, token.getType());

        return persons == null
                ? Map.of()
                : Collections.unmodifiableMap(persons);
    }

    private record Data() {
        
    }
}