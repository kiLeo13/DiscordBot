package bot.util.economy;

public record Action(
        long type,
        Message message
) {
    private record Message(String content) {}
}