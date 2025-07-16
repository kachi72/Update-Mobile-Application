package com.systemtech.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.systemtech.update.database.Article;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {
    private static final String TAG = "Utils";
    public static final String USER_FAV = "user_fav_posts";

    public static Utils instance;
    private final SharedPreferences sharedPreferences;

    public Utils(Context context){
        sharedPreferences = context.getSharedPreferences("user_favs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        Log.d(TAG, "Utils: creating shared preferences");

        if (null == getUserFav()){
            editor.putString(USER_FAV, gson.toJson(new ArrayList<Article>()));
            editor.apply();
        }
    }

    public ArrayList<Article> getUserFav() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Article>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(USER_FAV, null),type);
    }

    public void addToSharedPreferences(Article article){
        ArrayList<Article> articles = getUserFav();
        articles.add(article);
        Gson gson  = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_FAV, gson.toJson(articles));
        editor.apply();
    }

    public void removeFromSharedPreferences(Article article){
        ArrayList<Article> articles = getUserFav();
        articles.removeIf(a -> a.getDescription().equals(article.getDescription()));
        Gson gson  = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_FAV);
        editor.putString(USER_FAV, gson.toJson(articles));
        editor.apply();
    }

    public static Utils getInstance(Context context) {
        if (null == instance) {
            instance = new Utils(context);
        }
        return instance;
    }
}
