package com.famousnews.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.famousnews.models.Article;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void insertArticle(Article user);

    @Query("DELETE FROM news")
    void delete();

    @Query("SELECT * FROM news")
    List<Article> getArticle();
}
