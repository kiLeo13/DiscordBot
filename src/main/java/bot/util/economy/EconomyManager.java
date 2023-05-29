package bot.util.economy;

import bot.util.requests.Method;
import bot.util.requests.RequestManager;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.util.Map;

public class EconomyManager {
    private static final RequestManager manager = RequestManager.NewManager();
    private static final Gson gson = new Gson();
    private final String token;

    public EconomyManager(String token) {
        this.token = token;
    }

    public Balance getBalance(Member member) {
        String json = fetchBalance(member);
        return gson.fromJson(json, Balance.class);
    }

    public void setBalance(Member member, long bank, long cash, String reason) {
        try {
            setBalance(member, bank, cash, false, reason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetBalance(Member member) {
        setBalance(member, 0, 0, null);
    }

    public void updateBalance(Member member, long bank, long cash, String reason) {
        try {
            setBalance(member, cash, bank, true, reason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Leaderboard getLeaderboard(Guild guild) {
        String json = leaderboard(guild, 0);
        return gson.fromJson(json, Leaderboard.class);
    }

    public Leaderboard getLeaderboard(Guild guild, int page) {
        String json = leaderboard(guild, Math.max(page, 0));
        return gson.fromJson(json, Leaderboard.class);
    }

    private String fetchBalance(Member member) {
        String json = manager.requestAsString("https://unbelievaboat.com/api/v1/guilds/" + member.getGuild().getId() + "/users/" + member.getIdLong(),
                Map.of(
                "Authorization", this.token,
                "accept", "application/json"
                )
        );

        return json == null ? "" : json;
    }

    private void setBalance(Member member, long cash, long bank, boolean isUpdate, String reason) throws IOException {
        final String endpoint = "https://unbelievaboat.com/api/v1/guilds/" + member.getGuild().getId() + "/users/" + member.getId();

        manager.request(
                endpoint,
                Map.of(
                        "Authorization", this.token,
                        "accept", "application/json",
                        "content-type", "appliocation/json"
                ),
                isUpdate ? Method.PATCH : Method.PUT,
                String.format("""
                        {
                          "cash": %s
                          "bank": %s
                          "reason": %s
                        }
                        """,
                        cash > Integer.MAX_VALUE ? "Infinity" : cash,
                        bank > Integer.MAX_VALUE ? "Infinity" : bank,
                        reason == null ? "No reason provided" : reason
                )
        );
    }

    private String leaderboard(Guild guild, int page) {
        return manager.requestAsString("https://unbelievaboat.com/api/v1/guilds/" + guild.getId() + "/users/?sort=total&limit=1000&page=" + page,
                Map.of(
                        "Authorization", this.token,
                        "accept", "application/json"
                )
        );
    }
}