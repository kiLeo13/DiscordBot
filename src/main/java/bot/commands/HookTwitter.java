package bot.commands;

import java.io.IOException;
import java.util.HashMap;

import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import bot.util.managers.requests.Method;
import bot.util.managers.requests.RequestManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.ResponseBody;

@CommandPermission(permissions = Permission.ADMINISTRATOR)
public class HookTwitter implements CommandExecutor {
    private static final RequestManager manager = RequestManager.create();

    @Override
    public void run(Message message) {

        try {
            message.getChannel().sendMessage("```json\n" + request().string() + "\n```").queue();
        } catch (IOException e) {
            message.getChannel().sendMessage("Fodeu").queue();
            e.printStackTrace();
        }
    }

    private ResponseBody request() throws IOException {
        final HashMap<String, String> headers = new HashMap<>();
        long timestamp = (int) (System.currentTimeMillis() / 1000);

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "OAuth oauth_consumer_key=\"gh7FFKSAaHmhecJAnlYOhF7d4\",oauth_token=\"1165458764109176832-YN9jDrFE1wGAgovfGB8MTt26xBtJnF\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + timestamp + "\",oauth_nonce=\"wAGT1TIIKwD\",oauth_version=\"1.0\",oauth_signature=\"9jNyd2SMeu8W6czP4T%2BnRUi67R0%3D\"");

        return manager.request("https://api.twitter.com/1.1/account_activity/all/notifications/webhooks.json?url=http%3A%2F%2F177.193.43.179%2Fwebhook%2Ftwitter",
                    headers,
                    Method.POST,
                    null
                );
    }
}