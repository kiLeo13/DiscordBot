package bot.internal.managers.economy;

import java.util.List;

public record Leaderboard(
        List<Balance> users,
        int page,
        int total_pages
) {}