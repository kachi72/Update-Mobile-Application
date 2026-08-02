package com.systemtech.update.feeds;

import android.content.Context;

import androidx.annotation.NonNull;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public final class ArticleRepository {

    private final AppDatabase database;
    private final RssFeedClient feedClient;
    private final RssParser rssParser;

    public ArticleRepository(
            @NonNull AppDatabase database,
            @NonNull RssFeedClient feedClient,
            @NonNull RssParser rssParser
    ) {
        this.database = database;
        this.feedClient = feedClient;
        this.rssParser = rssParser;
    }

    @NonNull
    public static ArticleRepository create(@NonNull Context context) {
        return new ArticleRepository(
                AppDatabase.getInstance(context.getApplicationContext()),
                RssFeedClient.getInstance(),
                new RssParser()
        );
    }

    @NonNull
    public List<Article> refresh(@NonNull FeedSource source) throws IOException {
        byte[] payload = feedClient.download(source.getUrl());
        List<Article> articles;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(payload)) {
            articles = rssParser.parse(inputStream, source.getCategory());
        }

        if (articles.isEmpty()) {
            throw new IOException("Feed did not contain any valid articles: " + source.getKey());
        }

        database.runInTransaction(() -> {
            database.articleDao().deleteArticlesByCategory(source.getCategory());
            database.articleDao().insertAll(articles);
        });
        return articles;
    }
}
