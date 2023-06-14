package bot.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import bot.util.content.Channels;
import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@CommandPermission()
public class Tumaes implements CommandExecutor {
    private static final File file = new File("resources", "tumaes.yml");
    private static final Yaml yaml = new Yaml();

    @Override
    public void run(Message message) {
        
        TextChannel channel = message.getChannel().asTextChannel();
        Guild guild = message.getGuild();
        Member member = message.getMember();
        TextChannel allowed = guild.getTextChannelById(Channels.SALADA.id());
        String gif = getGif();

        if (allowed == null || (channel.getIdLong() != allowed.getIdLong() && !member.hasPermission(Permission.MANAGE_SERVER))) return;

        if (gif == null)
            channel.sendMessage("Nenhum gif adicionado à lista ou ocorreu um erro.").queue();
        else
            channel.sendMessage(gif).queue();
    }

    private String getGif() {
        try (InputStream stream = new FileInputStream(file)) {
            HashMap<String, List<String>> data = yaml.load(stream);
            List<String> gifs = data.get("gifs");
            
            if (gifs == null || gifs.isEmpty()) {
                return null;
            } else {
                int random = (int) (Math.random() * gifs.size());
                return gifs.get(random);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}