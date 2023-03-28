package bot.util;

public enum Messages {
    ERROR_CHANNEL_NOT_FOUND("Canal não encontrado! Conecte-se à um ou forneça um canal válido para usar este comando."),
    ERROR_MEMBER_NOT_FOUND("Membro não encontrado."),
    ERROR_REQUIRED_ROLES_NOT_FOUND("Um ou mais cargos necessários para esta operação não foram encontrados! Pedimos desculpas."),
    ERROR_HIERARCHY_HIGHER_ROLE("Um ou mais cargo estão acima do meu cargo mais alto! Por favor, ajuste a hierarquia do servidor corretamente.");

    final String message;

    Messages(String message) {
        this.message = message;
    }

    public String toMessage() {
        return this.message;
    }
}