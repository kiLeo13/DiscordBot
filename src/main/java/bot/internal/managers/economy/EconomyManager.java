package bot.internal.managers.economy;

import bot.internal.data.BotData;
import bot.internal.managers.requests.Method;
import bot.internal.managers.requests.RequestManager;
import com.google.gson.Gson;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class EconomyManager {
    private static final RequestManager manager = RequestManager.create();
    private static final Gson gson = new Gson();
    private final String token;

    public EconomyManager() {
        this.token = BotData.UNBELIEVABOAT_TOKEN;
    }

    /**
     * Gets the balance of a specified member.
     *
     * @param member The member to retrieve the balance.
     * @return A {@link Balance} instance containing the balance of the member or null if something goes wrong.
     */
    @Nullable
    public Balance getBalance(Member member) {
        String json = fetchBalance(member);
        return gson.fromJson(json, Balance.class);
    }

    /**
     * Sets the balance of a member to a new value.
     * 
     * @param member The member to have their balance set to a new value.
     * @param bank The new bank amount to be set.
     * @param cash The new cash amount to be set.
     * @param reason The reason of the change.
     * @return A {@link Balance} instance containing the new balance of the member or null if something goes wrong.
     */
    @Nullable
    public Balance setBalance(@NotNull Member member, long bank, long cash, @Nullable String reason) {
        try {
            String response = pushNewBalance(member, bank, cash, false, reason);

            return gson.fromJson(response, Balance.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resets the balance of the provided member.
     * <p>
     * <b>This action can definitely not be undone.</b>
     *
     * @param member The member to have their balance reset.
     * @return A {@link Balance} instance containing the new balance of the member or null if something goes wrong.
     */
    @Nullable
    public Balance resetBalance(@NotNull Member member) {
        return setBalance(member, 0, 0, null);
    }

    /**
     * Resets the balance of the provided member.
     * <p>
     * <b>This action can definitely not be undone.</b>
     * 
     * @param member The member to have their balance reset.
     * @param reason The reason for the reset.
     * @return A {@link Balance} instance containing the new balance of the member or null if something goes wrong.
     */
    @Nullable
    public Balance resetBalance(@NotNull Member member, @Nullable String reason) {
        return setBalance(member, 0, 0, reason);
    }

    /**
     * Updates the balance of a specific member.
     * <p>
     * Keep in mind that this method will <b>UPDATE</b> their balance, that is,
     * if you provide {@code (bank: 900, cash: -500)} so $900 will be <b>added</b> to their bank
     * and $500 will be <b>removed</b> from their cash.
     *
     * @param member The member to have their balance updated.
     * @param bank The bank amount to be updated.
     * @param cash The cash amount to be updated.
     * @param reason The reason of why that update was made.
     * @return A {@link Balance} instance containing the new balance of the member or null if something goes wrong.
     *
     * @see #setBalance(Member, long, long, String)
     */
    @Nullable
    public Balance updateBalance(@NotNull Member member, long bank, long cash, @Nullable String reason) {
        try {
            String response = pushNewBalance(member, cash, bank, true, reason);

            return gson.fromJson(response, Balance.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the {@link Leaderboard} of a certain guild.
     * <p>
     * This method retrieves 100 members per page as default.
     * <p>
     * <b>This method returns the first (0) page of the leaderboard</b>
     * 
     * @param guild The guild you want to get the leaderboard.
     * @return A {@link Leaderboard} instance containing all the leaderboard information or null if something goes wrong.
     * @see EconomyManager#getLeaderboard(Guild, int, int, int)
     */
    @Nullable
    public Leaderboard getLeaderboard(@NotNull Guild guild) {
        String json = fetchLeaderboard(guild, 0, 100, 0);
        return gson.fromJson(json, Leaderboard.class);
    }

    /**
     * Retrieves the {@link Leaderboard} of a certain guild.
     * 
     * @param guild The guild you want to get the leaderboard.
     * @param page The page of the leaderboard.
     * @param limit How many users can fit in a single page.
     * @param offset Where the leaderboard should start <i>(i.e: {@code offset:3} means it will start from the third user)</i>.
     * @throws {@link IllegalArgumentException} if {@code limit} or {@code offset} are less than zero.
     * @return A {@link Leaderboard} instance containing all the leaderboard information or null if something goes wrong.
     */
    @Nullable
    public Leaderboard getLeaderboard(@NotNull Guild guild, int page, int limit, int offset) {
        String json = fetchLeaderboard(guild, Math.max(page, 0), limit, offset);
        return gson.fromJson(json, Leaderboard.class);
    }

    private String fetchBalance(Member member) {
        String json = manager.requestAsString("https://unbelievaboat.com/api/v1/guilds/" + member.getGuild().getId() + "/users/" + member.getIdLong(),
                Map.of(
                "Authorization", this.token,
                "accept", "application/json"
                )
        );

        return json == null || json.isBlank() ? "{}" : json;
    }

    private String fetchLeaderboard(Guild guild, int page, int limit, int offset) throws IllegalArgumentException {
        if (limit < 0 || offset < 0)
            throw new IllegalArgumentException("Value 'limit' and 'offset' cannot be less than 0");

        return manager.requestAsString("https://unbelievaboat.com/api/v1/guilds/" + guild.getId() + "/users/?sort=total&limit=" + limit + "&page=" + page,
                Map.of(
                        "Authorization", this.token,
                        "accept", "application/json"
                )
        );
    }

    private String pushNewBalance(Member member, long cash, long bank, boolean isUpdate, String reason) throws IOException {
        final String endpoint = "https://unbelievaboat.com/api/v1/guilds/" + member.getGuild().getId() + "/users/" + member.getId();

        ResponseBody response = manager.request(
                endpoint,
                Map.of(
                        "Authorization", this.token,
                        "accept", "application/json",
                        "content-type", "application/json"
                ),
                isUpdate ? Method.PATCH : Method.PUT,
                String.format("""
                        {
                          "cash": "%s",
                          "bank": "%s",
                          "reason": "%s"
                        }
                        """,
                        cash > Integer.MAX_VALUE ? "Infinity" : cash,
                        bank > Integer.MAX_VALUE ? "Infinity" : bank,
                        reason == null || reason.isBlank() ? "`No reason`" : "`" +  reason.replace("`", "") + "`"
                )
        );

        String json = response.string();
        response.close();
        return json;
    }
}