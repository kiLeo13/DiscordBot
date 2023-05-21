package bot.commands;

import bot.util.Bot;
import bot.util.CommandExecutor;
import bot.util.CommandPermission;
import bot.util.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.List;

@CommandPermission()
public class RoleInfo implements CommandExecutor {

    @Override
    public void run(Message message) {
        
        String content = message.getContentRaw();
        Member member = message.getMember();
        String[] args = content.split(" ");
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        if (args.length < 2) {
            Bot.tempMessage(channel, Messages.ERROR_TOO_FEW_ARGUMENTS.message(), 10000);
            return;
        }

        Role role = Bot.findRole(guild, args[1]);

        if (role == null) {
            Bot.tempMessage(channel, "O cargo fornecido não existe.", 10000);
            return;
        }

        MessageCreateBuilder builder = new MessageCreateBuilder();
        MessageEmbed embed = embed(role);

        builder.setEmbeds(embed);
        builder.setContent("<@" + member.getIdLong() + ">");

        Message sent = channel.sendMessage(builder.build()).complete();

        guild.findMembersWithRoles(role).onSuccess(m -> {
            int size = m.size();
            int sizeOnline = m.stream().filter(mem -> !mem.getOnlineStatus().equals(OnlineStatus.OFFLINE)).toList().size();

            final EmbedBuilder newEmbed = new EmbedBuilder();

            newEmbed
                    .setTitle(embed.getTitle())
                    .setDescription(embed.getDescription())
                    .setColor(embed.getColor())
                    .setFooter(embed.getFooter() == null
                            ? ""
                            : embed.getFooter().getText(), embed.getFooter().getIconUrl());

            if (embed.getThumbnail() != null)
                newEmbed.setThumbnail(embed.getThumbnail().getUrl());

            for (int i = 0; i < embed.getFields().size(); i++) {
                if (i == embed.getFields().size() - 2) {
                    newEmbed.addField("👥 Membros", String.format("Total: `%s`\nOnline: `%s`",
                            size < 10 ? "0" + size : size,
                            sizeOnline < 10 ? "0" + sizeOnline : sizeOnline
                    ), true);
                    continue;
                }

                newEmbed.addField(embed.getFields().get(i));
            }

            sent.editMessageEmbeds(newEmbed.build()).queue();
        });
    }

    private MessageEmbed embed(Role role) {
        final EmbedBuilder builder = new EmbedBuilder();
        Guild guild = role.getGuild();
        long creation = role.getTimeCreated().toEpochSecond();

        String color = role.getColor() == null ? "`Nenhuma`" : "`#" + Integer.toHexString(role.getColor().getRGB()).substring(2).toUpperCase() + "`";
        int colorRed = role.getColor() == null ? 0 : role.getColor().getRed();
        int colorGreen = role.getColor() == null ? 0 : role.getColor().getGreen();
        int colorBlue = role.getColor() == null ? 0 : role.getColor().getBlue();

        builder
                .setTitle(role.getName())
                .setDescription("Informações do cargo <@&" + role.getIdLong() + ">.")
                .setColor(role.getColor())
                .addField("📅 Criação", "<t:" + creation + ">\n<t:" + creation + ":R>", true)
                .addField("💻 Role ID", "`" + role.getIdLong() + "`", true)
                .addField("🤖 Integração", role.isManaged() ? "`Sim`" : "`Não`", true)
                .addField(role.isMentionable() ? "🔔" : "🔕" + " Mencionável", role.isMentionable() ? "`Sim`" : "`Não`", true)
                .addField("📃 Mostrar Separadamente", role.isHoisted() ? "`Sim`" : "`Não`", true)
                .addField("🎨 Cor", String.format("HEX: `%s`\nRGB: `%s, %s, %s`",
                        color,
                        colorRed < 10 ? "0" + colorRed : String.valueOf(colorRed),
                        colorGreen < 10 ? "0" + colorGreen : String.valueOf(colorGreen),
                        colorBlue < 10 ? "0" + colorBlue : String.valueOf(colorBlue)
                ), true)
                .addField("👥 Membros", "Total: `Waiting...`\nOnline: `Waiting...`", true)
                .addField("🔒 Permissões", permissions(role), role.getPermissions().isEmpty())
                .setFooter(guild.getName(), guild.getIconUrl());

        RoleIcon icon = role.getIcon();
        if (icon != null)
            builder.setThumbnail(icon.getIconUrl());

        return builder.build();
    }

    private String permissions(Role role) {
        StringBuilder builder = new StringBuilder().append("```\n");
        List<Permission> permissions = role.getPermissions().stream().toList();

        if (permissions.isEmpty())
            return "`Nenhuma`";

        for (int i = 0; i < permissions.size(); i++) {
            if (i != 0) builder.append(", ");

            builder.append(permissions.get(i).getName());
        }

        builder.append(".\n```");
        return builder.toString();
    }
}