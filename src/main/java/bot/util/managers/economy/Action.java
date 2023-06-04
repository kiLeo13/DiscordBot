package bot.util.managers.economy;

public record Action(
        long type,
        Message message
) {
    private record Message(String content) {}
}