package com.systemtech.update.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.systemtech.update.database.Article;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM articles WHERE category = :category")
    List<Article> getArticlesByCategory(String category);

    @Insert
    void insertAll(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM articles WHERE category = :category")
    LiveData<List<Article>> getAllArticles(String category);

    @Query("SELECT * FROM articles")
   List<Article> getAllArticlesSync();

    @Query("DELETE FROM articles WHERE category = :category")
    void deleteArticlesByCategory(String category);

    @Query("DELETE FROM articles")
    void deleteAll();
}