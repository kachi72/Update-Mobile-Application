package com.systemtech.update.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articles")

public class Article {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private final String title;
    private final String date;
    private final String description;
    private final String link;

    @ColumnInfo(defaultValue = "") // Add default value to prevent issues
    public String category;   // <-- NEW FIELD

    public Article(String title, String date, String description, String link, String category) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.link = link;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }


    public String getDate() {
        return date;
    }



    public String getDescription() {
        return description;
    }


    public String getLink() {
        return link;
    }

    public String getCategory() {
        return category;
    }

    @NonNull
    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
