package com.josh_davey.news_app;

public class Article {
    public String ArticleNum;
    public String Title;
    public String Desc;

    public Article(String ArticleNum, String Title, String Desc)
    {
        this.ArticleNum = ArticleNum;
        this.Title = Title;
        this.Desc = Desc;
    }

    public String getArticleNum() {
        return ArticleNum;
    }

    public String getDesc() {
        return Desc;
    }

    public String getTitle() {
        return Title;
    }
}
