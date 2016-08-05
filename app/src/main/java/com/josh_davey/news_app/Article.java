package com.josh_davey.news_app;

public class Article {
    public String ArticleNum;
    public String ArticleTitle;
    public String ArticleDesc;

    public Article(String ArticleNum, String Title, String Desc)
    {
        this.ArticleNum = ArticleNum;
        this.ArticleTitle = Title;
        this.ArticleDesc = Desc;
    }

    public String getArticleNum() {
        return ArticleNum;
    }

    public String getArticleDesc() {
        return ArticleDesc;
    }

    public String getArticleTitle() {
        return ArticleTitle;
    }
}
