package bot.util.economy;

import java.util.List;

public record Leaderboard(
        List<Balance> users
) {}