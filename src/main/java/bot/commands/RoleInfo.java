package bot.commands;

import bot.internal.abstractions.BotCommand;
import bot.util.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.util.List;

public class RoleInfo extends BotCommand {
    private EmbedBuilder embedBuilder;

    public RoleInfo(String name) {
        super(true, 1, null, "{cmd} <role>", name);
    }

    @Override
    public void run(Message message, String[] args) {
        
        Member member = message.getMember();
        MessageChannelUnion channel = message.getChannel();
        Guild guild = message.getGuild();

        Role role = Bot.getRole(guild, args[0]);

        if (role == null) {
            Bot.tempMessage(channel, "O cargo fornecido não existe.", 10000);
            return;
        }

        MessageCreateBuilder builder = new MessageCreateBuilder();
        embed(role);

        builder.setEmbeds(embedBuilder.build());
        builder.setContent(member.getAsMention());

        channel.sendMessage(builder.build()).queue(m -> guild.findMembersWithRoles(role).onSuccess(ms -> {
            int size = ms.size();
            int sizeOnline = ms.stream().filter(mem -> !mem.getOnlineStatus().equals(OnlineStatus.OFFLINE)).toList().size();

            embedBuilder.getFields().set(embedBuilder.length() - 2, new MessageEmbed.Field(
                    "👥 Membros",
                    String.format("Total: `%s`\nOnline: `%s`",
                            size < 10 ? "0" + size : size,
                            sizeOnline < 10 ? "0" + sizeOnline : sizeOnline
                    ),
                    true
            ));

            m.editMessageEmbeds(embedBuilder.build()).queue();
        }));
    }

    private void embed(Role role) {
        embedBuilder = new EmbedBuilder();
        Guild guild = role.getGuild();
        long creation = role.getTimeCreated().toEpochSecond();

        String color = role.getColor() == null ? "`Nenhuma`" : "`#" + Integer.toHexString(role.getColor().getRGB()).substring(2).toUpperCase() + "`";
        int colorRed = role.getColor() == null ? 0 : role.getColor().getRed();
        int colorGreen = role.getColor() == null ? 0 : role.getColor().getGreen();
        int colorBlue = role.getColor() == null ? 0 : role.getColor().getBlue();

        embedBuilder
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
            embedBuilder.setThumbnail(icon.getIconUrl());
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