package com.systemtech.update.feeds;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RssFeedClient {

    private static final String USER_AGENT = "Update-Android-RSS-Reader/1.0";

    private static final OkHttpClient SHARED_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build();

    private static final RssFeedClient INSTANCE = new RssFeedClient(SHARED_CLIENT);

    private final OkHttpClient client;

    @VisibleForTesting
    public RssFeedClient(@NonNull OkHttpClient client) {
        this.client = client;
    }

    @NonNull
    public static RssFeedClient getInstance() {
        return INSTANCE;
    }

    @NonNull
    public byte[] download(@NonNull String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/rss+xml, application/xml, text/xml")
                .header("User-Agent", USER_AGENT)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Feed request failed with HTTP " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Feed response did not contain a body");
            }
            return body.bytes();
        }
    }
}
