package bot.util.requests;

import bot.util.Bot;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RequestManager {
    private RequestManager() {}

    public static RequestManager NewManager() {
        return new RequestManager();
    }

    /**
     * @param url The API endpoint.
     * @param headers The headers (optional).
     * @return An {@link InputStream} representation of the response body or null if something goes wrong.
     */
    public InputStream requestAsStream(String url, @Nullable Map<String, String> headers) {
        try {
            final ResponseBody body = request(url, headers, Method.GET, null);
            InputStream stream = body.byteStream();

            return stream;
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
    public String requestAsString(String url, @Nullable Map<String, String> headers) {
        try {
            final ResponseBody body = request(url, headers, Method.GET, null);
            String string = body.string();

            return string;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param url The API endpoint.
     * @param headers The headers (optional).
     * @param method The method to make the request.
     * @param requestBody The request body (optional for GET requests).
     * @return An open {@link ResponseBody} representation of the request.
     * @throws IOException If the request fails for some reason.
     */
    public ResponseBody request(String url, @Nullable Map<String, String> headers, Method method, String requestBody) throws IOException {
        final OkHttpClient client = new OkHttpClient();

        if (method != Method.GET && requestBody == null)
            throw new IllegalArgumentException("Request body cannot be null for " + method.name() + " operation");

        Request.Builder request = new Request.Builder()
                .url(url);

        switch (method) {
            case GET -> request.get();

            case PATCH -> request.patch(RequestBody.create(requestBody, MediaType.parse("application/json")));

            case PUT -> request.put(RequestBody.create(requestBody, MediaType.parse("application/json")));
        }

        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet())
                request.addHeader(key, headers.get(key));
        }

        return client.newCall(request.build()).execute().body();
    }
}