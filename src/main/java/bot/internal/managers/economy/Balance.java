package bot.internal.managers.economy;

public record Balance(
        String rank,
        String user_id,
        long cash,
        long bank,
        long total
) {}