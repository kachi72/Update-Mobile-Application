package com.systemtech.update.feeds;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class RssFeedClientTest {

    private MockWebServer server;
    private RssFeedClient client;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new RssFeedClient(new OkHttpClient());
    }

    @After
    public void tearDown() throws IOException {
        server.close();
    }

    @Test
    public void download_returnsSuccessfulResponseBytes() throws Exception {
        String body = "<rss version=\"2.0\"><channel /></rss>";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/rss+xml")
                .setBody(body));

        byte[] result = client.download(server.url("/feed").toString());

        assertArrayEquals(body.getBytes(StandardCharsets.UTF_8), result);
    }

    @Test
    public void download_throwsForNonSuccessfulResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(503)
                .setBody("Unavailable"));

        assertThrows(IOException.class,
                () -> client.download(server.url("/feed").toString()));
    }

    @Test
    public void download_throwsWhenTheCompleteCallTimesOut() {
        client = new RssFeedClient(new OkHttpClient.Builder()
                .callTimeout(200, TimeUnit.MILLISECONDS)
                .build());
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBodyDelay(1, TimeUnit.SECONDS)
                .setBody("<rss version=\"2.0\"><channel /></rss>"));

        assertThrows(InterruptedIOException.class,
                () -> client.download(server.url("/slow-feed").toString()));
    }
}
