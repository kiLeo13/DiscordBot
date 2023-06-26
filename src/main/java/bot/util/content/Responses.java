package bot.util.content;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public final class Responses {

    public static MessageEmbed ERROR_INFORMATION_NOT_FOUND = create("❌ A informação pedida não foi encontrada.", null);
    public static MessageEmbed ERROR_VOICE_CHANNEL_NOT_FOUND = create("❌ Canal de voz não encontrado", null);
    public static MessageEmbed ERROR_INVALID_ARGUMENTS = create("❌ Argumentos fornecidos são inválidos", null);
    public static MessageEmbed ERROR_TOO_FEW_ARGUMENTS = create("❌ Argumentos insuficientes", null);
    public static MessageEmbed ERROR_MEMBER_NOT_FOUND = create("❌ Membro não encontrado", null);
    public static MessageEmbed ERROR_USER_NOT_FOUND = create("❌ Usuário não encontrado", null);
    public static MessageEmbed ERROR_REQUIRED_ROLES_NOT_FOUND = create("❌ Cargos não encontrados", "Um ou mais cargos necessários para esta operação não foram encontrados! Pedimos desculpas pelo ocorrido.");

    public static MessageEmbed error(String reason, String description, String img) {
        return template(reason, description, img, Color.RED)
                .build();
    }

    public static MessageEmbed warn(String subj, String desc, String img) {
        return template(subj, desc, img, Color.YELLOW)
                .build();
    }

    public static MessageEmbed success(String subj, String desc, String img) {
        return template(subj, desc, img, Color.GREEN)
                .build();
    }

    private static EmbedBuilder template(String subj, String desc, String img, Color color) {
        return new EmbedBuilder()
                .setAuthor(subj, null, img)
                .setDescription(desc)
                .setColor(color);
    }

    private static MessageEmbed create(String reason, String description) {
        return new EmbedBuilder()
                .setAuthor(reason)
                .setDescription(description)
                .setColor(Color.RED)
                .build();
    }
}