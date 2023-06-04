package bot.commands;

import bot.Main;
import bot.util.Bot;
import bot.util.content.Channels;
import bot.util.content.Messages;
import bot.util.interfaces.BotScheduler;
import bot.util.interfaces.SlashExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ColorRole implements SlashExecutor, BotScheduler {

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

        // Usage: /color <user> <color>
        Member member = event.getMember();
        Guild guild = event.getGuild();
        Member target = event.getOption("member").getAsMember();
        Role color = guild.getRoleById(event.getOption("color").getAsString());

        if (member == null) return;

        if (target == null) {
            event.reply(Messages.ERROR_MEMBER_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        if (color == null) {
            event.reply(Messages.ERROR_REQUIRED_ROLES_NOT_FOUND.message()).setEphemeral(true).queue();
            return;
        }

        // Guild related
        guild.addRoleToMember(target, color).queue();
        store(target, color);

        event.reply("Cargo `" + color.getName() + "` foi adicionado à `" + target.getEffectiveName() + "`.").setEphemeral(false).setEphemeral(true).queue();
        Bot.tempMessage(guild.getTextChannelById(Channels.CHANNEL_BANK.id()), "<@" + target.getIdLong() + "> Você recebeu o cargo `" + color.getName() + "` com sucesso!", 300000);

        logAdd(member, target, color, guild);
    }

    private void logAdd(Member author, Member target, Role color, Guild guild) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        EmbedBuilder builder = new EmbedBuilder();

        TextChannel channel = guild.getTextChannelById(Channels.LOG_COLOR_ROLE_COMMAND_CHANNEL.id());

        builder
            .setTitle("Membro: `" + target.getUser().getAsTag() + "`!")
            .setThumbnail(target.getUser().getAvatarUrl())
            .setColor(color.getColor())
            .setDescription("Cargos de cor são removidos após `" + REMOVAL + "` dias.")
            .addField("🏷 Cargo", "<@&" + color.getIdLong() + ">", true)
            .addField("👑 Staff", "`" + author.getUser().getAsTag() + "`", true)
            .addField("💻 Target ID", "`" + target.getIdLong() + "`", true)
            .addField("📅 Adicionado", "`" + date.format(now) + " às " + time.format(now) + "`", true)
            .addField("📅 Será removido", "`" + date.format(now.plusDays(REMOVAL)) + " às " + time.format(now.plusDays(REMOVAL)) + "`", true)
            .setFooter(guild.getName(), guild.getIconUrl());

        if (channel != null) channel.sendMessageEmbeds(builder.build()).queue();
    }

    private static void logRemove(String guildId, String memberId, String roleId) {
        EmbedBuilder builder = new EmbedBuilder();
        JDA jda = Main.getApi();
        Guild guild = jda.getGuildById(guildId);

        if (guild == null) return;
        Role role = guild.getRoleById(roleId);
        TextChannel log = guild.getTextChannelById(Channels.LOG_COLOR_ROLE_COMMAND_CHANNEL.id());
        TextChannel bank = guild.getTextChannelById(Channels.CHANNEL_BANK.id());

        if (role == null) return;

        guild.retrieveMemberById(memberId).queue(m -> {
            builder
                    .setTitle("`" + m.getUser().getAsTag() + "`")
                    .setThumbnail(m.getUser().getAvatarUrl())
                    .setColor(Color.RED)
                    .setDescription("Cargo de cor foi removido.")
                    .addField("🏷 Cargo", "<@&" + role.getIdLong() + ">", true)
                    .addField("💻 Target ID", "`" + m.getId() + "`", true)
                    .setFooter(guild.getName(), guild.getIconUrl());

            if (log != null) log.sendMessageEmbeds(builder.build()).queue();
            if (bank != null) bank.sendMessage("<@" + m.getIdLong() + "> `" + REMOVAL + "` dias se passaram, cargo `" + role.getName() + "` foi removido!").queue();
        });
    }

    public static void store(Member member, Role element) {
        Map<String, Data> persons = read();
        LocalDateTime now = LocalDateTime.now();

        persons.put(member.getId(), new Data(
                now.toEpochSecond(ZoneOffset.UTC),
                now.plusDays(REMOVAL).toEpochSecond(ZoneOffset.UTC),
                member.getId(),
                member.getGuild().getId(),
                element.getId())
        );

        String json = gson.toJson(persons);
        write(json);
    }

    public static void store(Map<String, Data> persons) {
        write(gson.toJson(persons));
    }

    private static void write(String json) {
        try (
            OutputStream out = Files.newOutputStream(Path.of(file.getPath()));
            Writer writer = new OutputStreamWriter(out);
        ) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Data> read() {
        String json;
        try { json = String.join("", Files.readAllLines(Path.of(file.getPath()))); }
        catch (IOException e) { return new HashMap<>(); }

        TypeToken<Map<String, Data>> token = new TypeToken<>(){};
        Map<String, Data> persons = gson.fromJson(json, token.getType());

        // Returns a new HashMap instead of null
        return persons == null
            ? new HashMap<>()
            : persons;
    }

    public record Data(long now, long remove, String member, String guildId, String roleId) {}

    @Override
    public void perform() {
        Bot.setInterval(() -> {
            Map<String, ColorRole.Data> information = ColorRole.read();
            LocalDateTime now = LocalDateTime.now();

            Map<String, ColorRole.Data> toWrite = new HashMap<>();

            for (String s : information.keySet()) {
                ColorRole.Data data = information.get(s);

                if (data.remove() > now.toEpochSecond(ZoneOffset.UTC)) {
                    toWrite.put(s, information.get(s));
                } else {
                    JDA api = Main.getApi();
                    Guild guild = api.getGuildById(data.guildId());

                    if (guild == null) return;

                    Role role = guild.getRoleById(data.roleId());

                    if (role == null) return;

                    guild.retrieveMemberById(data.member()).queue(m -> {
                        guild.removeRoleFromMember(m, role).queue();

                        logRemove(data.guildId, s, data.roleId);
                        Bot.log(m.getUser().getAsTag() + " teve o cargo de cor " + role.getName() + " removido.", false);
                    });
                }
            }

            ColorRole.store(toWrite);
        }, 60000);
    }
}