package bot.util;

public enum Messages {
    ERROR_VOICE_CHANNEL_NOT_FOUND("Canal não encontrado! Conecte-se à um ou forneça um canal válido para usar este comando."),
    ERROR_TOO_FEW_ARGUMENTS("Argumentos insuficientes."),
    ERROR_MEMBER_NOT_FOUND("Membro não encontrado."),
    ERROR_REQUIRED_ROLES_NOT_FOUND("Um ou mais cargos necessários para esta operação não foram encontrados! Pedimos desculpas."),
    ERROR_SAME_ARGUMENTS_PROVIDED("O mesmo valor foi fornecido em ambos ou mais argumentos."),
    ERROR_VOICE_CHANNEL_EMPTY("O canal de voz fornecido está vazio.");

    final String message;

    Messages(String message) {
        this.message = message;
    }

    public String message() {
        return this.message;
    }
}