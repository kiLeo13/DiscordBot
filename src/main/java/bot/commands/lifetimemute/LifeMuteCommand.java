package bot.commands.lifetimemute;

import bot.data.BotFiles;
import bot.util.Bot;
import bot.util.interfaces.CommandExecutor;
import bot.util.annotations.CommandPermission;
import bot.util.Messages;
import bot.util.requests.DiscordManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandPermission(permission = Permission.BAN_MEMBERS)
public class LifeMuteCommand implements CommandExecutor {
    private static final DiscordManager discord = DiscordManager.NewManager();
    private static final File mutedMembersFile = new File(BotFiles.DIR, "lifemuted.json");

    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run(Message message) {

        String content = message.getContentRaw();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();
        Member member = message.getMember();
        Role muted = discord.roleOf(guild, "592465045686845480");

        if (args.length < 2 || (args.length < 3 && content.endsWith("--info"))) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        String reason = args.length < 3
                ? "No reason provided."
                : content.substring(args[0].length() + args[1].length() + 2);

        Member target = Bot.member(guild, args[1]);

        // If the intention is to retrieve information about someone's muted
        if (content.endsWith("--info")) {
            MutedMember stats = stats(args[1]);

            if (stats == null) Bot.tempMessage(channel, "O membro informado não está silenciado para sempre ou não foi encontrado.", 10000);
            else {
                final MessageCreateBuilder builder = new MessageCreateBuilder();

                builder.setContent("<@" + member.getId() + ">");
                builder.setEmbeds(embed(stats, target, guild));

                channel.sendMessage(builder.build()).queue();
            }

            return;
        }

        /* If the intetion is to mute or unmute someone */
        if (target == null) {
            Bot.tempMessage(channel, Messages.ERROR_MEMBER_NOT_FOUND.message(), 10000);
            return;
        }

        if (target.hasPermission(Permission.MANAGE_SERVER) || target.getUser().isBot()) {
            Bot.tempMessage(channel, "Você não pode silenciar usuários que têm a permissão `Manage Server` ou bots.", 10000);
            return;
        }

        if (isLifeMuted(target)) {
            unmute(target);

            final MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(100, 255, 100))
                    .setAuthor(target.getUser().getAsTag() + " foi dessilenciado", null, target.getUser().getAvatarUrl())
                    .build();

            if (muted != null)
                guild.modifyMemberRoles(target, null, List.of(muted)).queue();
            else
                Bot.log("<YELLOW>Cargo '592465045686845480' não foi encontrado ao usar o comando lifetime-mute. Ignorando cargo...");

            channel.sendMessageEmbeds(embed).queue();
        } else {
            mute(target, member, reason);

            final MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(255, 100, 100))
                    .setAuthor(target.getUser().getAsTag() + " foi silenciado", null, target.getUser().getAvatarUrl())
                    .setDescription("**Motivo:** " + reason)
                    .build();

            // Disconnects the muted member if currently in a voice channel
            if (target.getVoiceState() != null && target.getVoiceState().inAudioChannel())
                guild.kickVoiceMember(target).queue();

            if (muted != null)
                guild.modifyMemberRoles(target, List.of(muted), null).queue();
            else
                Bot.log("<YELLOW>Cargo '592465045686845480' não foi encontrado ao usar o comando lifetime-mute. Ignorando cargo...");

            channel.sendMessageEmbeds(embed).queue();
        }
    }

    public static boolean isLifeMuted(Member member) {
        if (member == null)
            return false;

        String id = member.getId();
        Map<String, MutedMember> muted = mapJson(Bot.read(mutedMembersFile));

        for (String m : muted.keySet()) {
            if (id.equals(m))
                return true;
        }

        return false;
    }

    private void mute(Member target, Member moderator, String reason) {
        final Map<String, MutedMember> muted = mapJson(Bot.read(mutedMembersFile));
        LocalDateTime now = LocalDateTime.now();

        muted.put(target.getId(), new MutedMember(
                now.toEpochSecond(ZoneOffset.UTC),
                reason,
                moderator.getId(),
                new UserData(target.getUser().getAsTag(), target.getId(), target.getUser().getAvatarUrl())
        ));

        String toWrite = gson.toJson(muted);
        Bot.write(toWrite, mutedMembersFile);
    }

    private void unmute(Member member) {
        final Map<String, MutedMember> muted = mapJson(Bot.read(mutedMembersFile));

        muted.remove(member.getId());

        String toWrite = gson.toJson(muted);
        Bot.write(toWrite, mutedMembersFile);
    }

    private MutedMember stats(String regex) {
        String id = regex.replaceAll("[^0-9]+", "");

        if (id.isBlank())
            return null;

        final Map<String, MutedMember> muted = mapJson(Bot.read(mutedMembersFile));

        for (String m : muted.keySet()) {
            if (m.equals(id))
                return muted.get(m);
        }

        return null;
    }

    private static Map<String, MutedMember> mapJson(String json) {
        TypeToken<Map<String, MutedMember>> token = new TypeToken<>() {};

        final Map<String, MutedMember> muted = gson.fromJson(json, token.getType());

        return muted == null
                ? new HashMap<>()
                : muted;
    }

    private MessageEmbed embed(MutedMember muted, Member target, Guild guild) {
        final EmbedBuilder builder = new EmbedBuilder();
        UserData mutedUser = muted.target;

        builder
                .setTitle("✨ Lifetime-Mute")
                .setThumbnail(mutedUser.avatar)
                .setDescription("Informações do mute de `" + mutedUser.tag + "`.")
                .addField("🤔 Motivo", muted.reason, true)
                .addField("📅 Silenciado (UTC)", "<t:" + muted.dateMuted + ">\n(<t:" + muted.dateMuted + ":R>)", true)
                .addField("👑 Moderador", "<@" + muted.moderatorId + ">\n`" + muted.moderatorId + "`", true)
                .addField("🌐 Está no Servidor", target == null ? "❌ Não" : "✅ Sim", true)
                .addField("💻 User ID", "`" + mutedUser.id + "`", true)
                .setFooter(guild.getName(), guild.getIconUrl())
                .setColor(new Color(255, 100, 100));

        return builder.build();
    }

    private record MutedMember(long dateMuted, String reason, String moderatorId, UserData target) {}

    private record UserData(String tag, String id, String avatar) {}
}