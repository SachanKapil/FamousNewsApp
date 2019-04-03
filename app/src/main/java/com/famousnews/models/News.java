package com.famousnews.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {

    private String status;

    private int totalResults;

    @SerializedName("articles")
    private List<Article> article;

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public List<Article> getArticle() {
        return article;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setArticle(List<Article> article) {
        this.article = article;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}

