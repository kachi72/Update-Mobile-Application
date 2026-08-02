package com.systemtech.update.feeds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public enum FeedSource {
    CYBER_SECURITY(
            "cyber_security",
            "CyberSecurity",
            "https://feeds.feedburner.com/TheHackersNews"
    ),
    AI_ML(
            "ai_ml",
            "AI/ML",
            "https://www.technologyreview.com/feed"
    ),
    SOFTWARE_ENGINEERING(
            "software_engineering",
            "Software Engineering",
            "https://www.toptal.com/blog.rss"
    ),
    NETWORKING(
            "networking",
            "Networking",
            "https://blogs.cisco.com/networking/feed"
    ),
    DATA_SCIENCE(
            "data_science",
            "Data Science",
            "https://www.kdnuggets.com/tag/data-science/feed"
    ),
    UI_UX(
            "ui_ux",
            "UI/UX",
            "https://www.nngroup.com/feed/rss/"
    );

    private final String key;
    private final String category;
    private final String url;

    FeedSource(String key, String category, String url) {
        this.key = key;
        this.category = category;
        this.url = url;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @Nullable
    public static FeedSource fromKey(@Nullable String key) {
        if (key == null) {
            return null;
        }

        for (FeedSource source : values()) {
            if (source.key.equals(key)) {
                return source;
            }
        }
        return null;
    }
}
