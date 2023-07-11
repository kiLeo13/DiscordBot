package bot.internal.managers.requests;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RequestManager {

    /**
     * @param url The API endpoint.
     * @param headers The headers (optional).
     * @return An {@link InputStream} representation of the response body or null if something goes wrong.
     */
    public InputStream requestStream(String url, @Nullable Map<String, String> headers) {
        try {
            final ResponseBody body = request(url, headers, Method.GET, null);
            byte[] bytes = body.bytes();
            InputStream response = new ByteArrayInputStream(bytes);

            body.close();
            return response;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param url The API endpoint.
     * @param headers The headers (optional).
     * @return The {@link String} representation of the response body or an empty String if something goes wrong.
     */
    public String requestString(String url, @Nullable Map<String, String> headers) {
        try {
            final ResponseBody body = request(url, headers, Method.GET, null);
            String response = body.string();

            body.close();
            return response;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Returns a {@link ResponseBody} representation of the request.
     * <p>
     * <b>You MUST close every {@link ResponseBody} after using it.</b>
     *
     * @param url The endpoint.
     * @param headers The headers (optional).
     * @param method The method to make the request.
     * @param requestBody The request body (optional for GET requests).
     * @return An open {@link ResponseBody} representation of the request.
     * @throws IOException If the request fails for some reason.
     */
    public ResponseBody request(@NotNull String url, @Nullable Map<String, String> headers, @NotNull Method method, @Nullable String requestBody) throws IOException {
        final OkHttpClient client = new OkHttpClient();

        Request.Builder request = new Request.Builder().url(url);

        if (requestBody != null && !requestBody.isEmpty())
            request.method(method.name(), RequestBody.create(requestBody, MediaType.parse("application/json")));

        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet())
                request.addHeader(key, headers.get(key));
        }

        return client.newCall(request.build()).execute().body();
    }
}