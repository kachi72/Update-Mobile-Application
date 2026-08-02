package com.systemtech.update.database;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
public class ArticleDaoTest {

    private AppDatabase database;
    private ArticleDao articleDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        articleDao = database.articleDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void hasAnyArticles_reflectsWhetherTheDatabaseContainsArticles() {
        assertThat(articleDao.hasAnyArticles()).isFalse();

        Article article = new Article(
                "Test article",
                "2026-08-02",
                "Test description",
                "https://example.com/article",
                "CyberSecurity"
        );
        articleDao.insertAll(Collections.singletonList(article));

        assertThat(articleDao.hasAnyArticles()).isTrue();

        articleDao.deleteAll();

        assertThat(articleDao.hasAnyArticles()).isFalse();
    }
}
